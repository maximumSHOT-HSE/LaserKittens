package ru.hse.team.game.gamelogic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;

/**
 * Input processor for game screen.
 * By touch on player user can drag it.
 * Touching not on a player image leads to shoot(by a player cat)
 *  in direction of touch
 * If accelerometer enabled player won't be dragged and it's position
 *  will change according to phone tilt
 * Game status timer starts on first touch or movement with accelerometer
 */
public class GameScreenInputProcessor implements InputProcessor {
    private final LaserKittens laserKittens;
    private final Entity focusedPlayer;
    private final AbstractLevel abstractLevel;
    private final OrthographicCamera camera;

    private boolean dragging;
    private int draggingPointer = -1;
    private final Vector3 position = new Vector3();

    /** won't change during abstractLevel */
    private boolean enabledAccelerometer;

    private final Vector3 draggingPosition = new Vector3();
    private final Vector2 draggingStartedDiff = new Vector2();

    private volatile MouseJoint mouseJoint = null;
    /** special dummy body for mousejoint */
    private final Body ground;

    private final Vector2 target = new Vector2();

    public boolean isDragging() {
        return dragging;
    }

    public GameScreenInputProcessor(
        LaserKittens laserKittens,
        AbstractLevel abstractLevel,
        OrthographicCamera camera
    ) {
        this.laserKittens = laserKittens;
        this.focusedPlayer = abstractLevel.getPlayer();
        this.abstractLevel = abstractLevel;
        this.camera = camera;

        enabledAccelerometer = laserKittens.getPreferences().isEnabledAccelerometer();

        ground = BodyFactory.getBodyFactory(abstractLevel.getWorld())
            .newCircleBody(
                new Vector2(
                    0,
                    abstractLevel.getLevelHeightInScreens() *
                            RenderingSystem.getScreenSizeInMeters().y * 2
                ),
                0.1f,
                BodyDef.BodyType.StaticBody,
                true
           );
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK) {
            Screen gameScreen = laserKittens.getScreen();
            laserKittens.changeScreen(LaserKittens.ScreenType.CHOOSE_LEVEL_SCREEN);
            gameScreen.dispose();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    private float distance2D(Vector2 a, Vector2 b) {
        return a.dst(b);
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    private boolean clickInPlayerRegion() {
        BodyComponent playerBodyComponent = Mapper.bodyComponent.get(focusedPlayer);
        if (playerBodyComponent == null) return false;
        Body playerBody = playerBodyComponent.body;
        if (playerBody == null) return false;


        float radius = abstractLevel.getPlayerRadius();
        return distance2D(playerBody.getPosition(), new Vector2(position.x, position.y)) < radius;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(position.set(screenX, screenY, 0));
        abstractLevel.getGameStatus().start();

        if (!clickInPlayerRegion()) {
            if (abstractLevel.isMultiplayer()) {
                System.out.println("Multiplayer");
                Gdx.app.postRunnable(() -> abstractLevel.shoot(position.x, position.y));
            } else {
                abstractLevel.shoot(position.x, position.y);
            }
            Sound laser = laserKittens.getAssetManager()
                    .getSound(KittensAssetManager.Sounds.LASER_SOUND);
            laser.play(laserKittens.getPreferences().getSoundVolume());
            return true;
        }

        if (dragging) {
            return false;
        }

        if (enabledAccelerometer) {
            return false;
        }

        BodyComponent playerBodyComponent = Mapper.bodyComponent.get(focusedPlayer);
        if (playerBodyComponent == null) return false;
        Body playerBody = playerBodyComponent.body;
        if (playerBody == null) return false;

        dragging = true;
        draggingPointer = pointer;
        float playerX = playerBody.getPosition().x;
        float playerY = playerBody.getPosition().y;
        draggingStartedDiff.set(position.x - playerX, position.y - playerY);

        MouseJointDef def = new MouseJointDef();
        def.bodyA = ground;
        def.bodyB = playerBody;
        def.collideConnected = true;
        def.maxForce = 1000.0f * playerBody.getMass();
        def.target.set(playerX, playerY);

        Gdx.app.postRunnable(() -> {
            mouseJoint = (MouseJoint) abstractLevel.getWorld().createJoint(def);
        });
        playerBody.setAwake(true);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        camera.unproject(position.set(screenX, screenY, 0));

        if (pointer != draggingPointer) {
            return false;
        }

        if (enabledAccelerometer) {
            return false;
        }

        dragging = false;
        draggingPointer = -1;

        MouseJoint joint = mouseJoint;
        if (joint != null) {
            BodyComponent playerBodyComponent = Mapper.bodyComponent.get(focusedPlayer);
            if (playerBodyComponent == null) return false;
            Body playerBody = playerBodyComponent.body;
            if (playerBody == null) return false;

            Gdx.app.postRunnable(() -> {
                abstractLevel.getWorld().destroyJoint(joint);
                playerBody.setLinearVelocity(0, 0);
            });
        }
        mouseJoint = null;

        return true;
    }

    public void touchDraggedExplicitly() {
        if (enabledAccelerometer) return;
        if (draggingPointer != -1) {
            int screenX = Gdx.input.getX(draggingPointer);
            int screenY = Gdx.input.getY(draggingPointer);
            touchDragged(screenX, screenY, draggingPointer);
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!dragging || pointer != draggingPointer) {
            return false;
        }
        if (enabledAccelerometer) {
            return false;
        }

        camera.unproject(draggingPosition.set(screenX, screenY, 0));
        draggingPosition.x -= draggingStartedDiff.x;
        draggingPosition.y -= draggingStartedDiff.y;

        MouseJoint joint = mouseJoint;
        if (joint != null) {
            joint.setTarget(target.set(draggingPosition.x, draggingPosition.y));
        }

        return true;
    }

    public void moveWithAccelerometer(float delta) {
        if (!enabledAccelerometer) {
            return;
        }

        abstractLevel.getGameStatus().start();

        final float accelerometerX = Gdx.input.getAccelerometerX();
        final float accelerometerY = Gdx.input.getAccelerometerY();

        BodyComponent playerBodyComponent = Mapper.bodyComponent.get(focusedPlayer);
        if (playerBodyComponent == null) return;
        Body playerBody = playerBodyComponent.body;
        if (playerBody == null) return;

        if (Math.abs(accelerometerX) < 0.1 && Math.abs(accelerometerY) < 0.1) {
            playerBody.applyForce(
                -playerBody.getLinearVelocity().x * delta * 1e3f * playerBody.getMass(),
                -playerBody.getLinearVelocity().y * delta * 1e3f * playerBody.getMass(),
                playerBody.getPosition().x,
                playerBody.getPosition().y,
                true
            );
            return;
        }

        float forceX = Math.max(2, Math.abs(accelerometerX)) * delta * 1e3f * playerBody.getMass();
        float forceY = Math.max(2, Math.abs(accelerometerY)) * delta * 1e3f * playerBody.getMass();

        playerBody.applyForce(
            -Math.signum(accelerometerX) * forceX,
            -Math.signum(accelerometerY) * forceY,
            accelerometerX + playerBody.getPosition().x,
            accelerometerY + playerBody.getPosition().y,
            true
        );
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}