package com.example.learning.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.example.learning.LaserKittens;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevel;

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

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    private boolean clickInPlayerRegion() {
        float textureX = Mapper.transformComponent.get(focusedPlayer).position.x;
        float textureY = Mapper.transformComponent.get(focusedPlayer).position.y;
        float textureWidth = RenderingSystem.pixelsToMeters(Mapper.textureComponent.get(focusedPlayer).region.getRegionWidth())
                * Mapper.transformComponent.get(focusedPlayer).scale.x;
        float textureHeight = RenderingSystem.pixelsToMeters(Mapper.textureComponent.get(focusedPlayer).region.getRegionHeight())
                * Mapper.transformComponent.get(focusedPlayer).scale.y;

        float originX = textureWidth / 2f;
        float originY = textureHeight / 2f;


        Rectangle textureBounds = new Rectangle(textureX - originX,textureY - originY,textureWidth,textureHeight);
        return textureBounds.contains(position.x, position.y);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(position.set(screenX, screenY, 0));

        if (!clickInPlayerRegion()) {
            level.getFactory().shoot(position.x, position.y);
            Sound laser = laserKittens.assetManager.manager.get(KittensAssetManager.laserSound, Sound.class);
            laser.play(laserKittens.getPreferences().getSoundVolume());
            return true;
        }

        if (dragging) {
            return false;
        }

        Body playerBody = Mapper.bodyComponent.get(focusedPlayer).body;
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

        dragging = false;
        draggingPointer = -1;
        if (mouseJoint != null) {
            world.destroyJoint(mouseJoint);
            mouseJoint = null;
        }
        Mapper.bodyComponent.get(focusedPlayer).body.setLinearVelocity(0, 0);

        return true;
    }

    public void touchDraggedExplicitly() {

        if (draggingPointer != -1) {
            int screenX = Gdx.input.getX(draggingPointer);
            int screenY = Gdx.input.getY(draggingPointer);

            touchDragged(screenX, screenY, draggingPointer);
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if (!dragging || pointer != draggingPointer) return false;


        camera.unproject(draggingPosition.set(screenX, screenY, 0));
        draggingPosition.x -= draggingStartedDiff.x;
        draggingPosition.y -= draggingStartedDiff.y;
        mouseJoint.setTarget(target.set(draggingPosition.x, draggingPosition.y));

        return true;
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