package ru.hse.team.game;

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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import ru.hse.team.LaserKittens;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;

/**
 * Input processor for game screen.
 * Dragging player and shooting implemented here
 */
public class GameScreenInputProcessor implements InputProcessor {

    private LaserKittens laserKittens;
    private Entity focusedPlayer;
    private AbstractLevel level;
    private OrthographicCamera camera;
    private World world;

    private boolean dragging;
    private int draggingPointer = -1;
    private Vector3 position = new Vector3();

    /** won't change during level */
    private final boolean enabledAccelerometer;

    private Vector3 draggingPosition = new Vector3();
    private Vector2 draggingStartedDiff = new Vector2();

    private MouseJoint mouseJoint = null;
    /** special dummy body for mousejoint */
    private final Body ground;

    public static final int MAXIMUM_NUMBER_OF_TOUCHES = 20;

    private Vector2 target = new Vector2();

    public GameScreenInputProcessor(LaserKittens laserKittens, AbstractLevel level, OrthographicCamera camera) {
        this.laserKittens = laserKittens;
        this.focusedPlayer = level.getFactory().getPlayer();
        this.level = level;
        this.camera = camera;
        this.world = level.getFactory().getWorld();

        enabledAccelerometer = laserKittens.getPreferences().isEnabledAccelerometer();

        ground = BodyFactory.getBodyFactory(this.world)
        .newCircleBody(
            new Vector2(0, level.getFactory().getLevelHeightInScreens() *
                    RenderingSystem.getScreenSizeInMeters().y * 2),
                0.1f,
                BodyDef.BodyType.StaticBody,
                true
        );
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            final Screen gameScreen = laserKittens.getScreen();
            laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.CHOOSE_LEVEL_SCREEN);
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
        Body body = Mapper.bodyComponent.get(focusedPlayer).body;
        float radius = level.getFactory().getPlayerRadius();

        return distance2D(body.getPosition(), new Vector2(position.x, position.y)) < radius;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(position.set(screenX, screenY, 0));

        if (!clickInPlayerRegion()) {
            level.shoot(position.x, position.y);
//            Sound laser = laserKittens.assetManager.manager.get(KittensAssetManager.laserSound, Sound.class);
//            laser.play(laserKittens.getPreferences().getSoundVolume());
            return true;
        }

        if (dragging) {
            return false;
        }

        if (enabledAccelerometer) {
            return false;
        }

        final BodyComponent playerBodyComponent = Mapper.bodyComponent.get(focusedPlayer);
        if (playerBodyComponent == null) return false;
        final Body playerBody = playerBodyComponent.body;
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
        mouseJoint = (MouseJoint)world.createJoint(def);
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
        if (mouseJoint != null) {
            world.destroyJoint(mouseJoint);
            mouseJoint = null;
        }

        final BodyComponent playerBodyComponent = Mapper.bodyComponent.get(focusedPlayer);
        if (playerBodyComponent == null) return false;
        final Body playerBody = playerBodyComponent.body;
        if (playerBody == null) return false;

        playerBody.setLinearVelocity(0, 0);

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

        if (!dragging || pointer != draggingPointer) return false;
        if (enabledAccelerometer) return false;

        camera.unproject(draggingPosition.set(screenX, screenY, 0));
        draggingPosition.x -= draggingStartedDiff.x;
        draggingPosition.y -= draggingStartedDiff.y;
        mouseJoint.setTarget(target.set(draggingPosition.x, draggingPosition.y));

        return true;
    }

    public void moveWithAccelerometer(float delta) {
        if (!enabledAccelerometer) return;

        final float accelerometerX = Gdx.input.getAccelerometerX();
        final float accelerometerY = Gdx.input.getAccelerometerY();

        final BodyComponent playerBodyComponent = Mapper.bodyComponent.get(focusedPlayer);
        if (playerBodyComponent == null) return;
        final Body playerBody = playerBodyComponent.body;
        if (playerBody == null) return;

        if (Math.abs(accelerometerX) < 0.3 && Math.abs(accelerometerY) < 0.3) {
            playerBody.applyForce(
                    -playerBody.getLinearVelocity().x * delta * 1e3f * playerBody.getMass(),
                    -playerBody.getLinearVelocity().y * delta * 1e3f * playerBody.getMass(),
                    playerBody.getPosition().x, playerBody.getPosition().y, true);
            return;
        }

        float forceX = Math.max(2, Math.abs(accelerometerX)) * delta * 1e3f * playerBody.getMass();
        float forceY = Math.max(2, Math.abs(accelerometerY)) * delta * 1e3f * playerBody.getMass();

        playerBody.applyForce(-Math.signum(accelerometerX) * forceX, -Math.signum(accelerometerY) * forceY                                                                      ,
                accelerometerX + playerBody.getPosition().x, accelerometerY + playerBody.getPosition().y, true);
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