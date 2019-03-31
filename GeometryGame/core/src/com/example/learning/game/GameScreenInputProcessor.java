package com.example.learning.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
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
import com.example.learning.game.gamelogic.components.PlayerComponent;
import com.example.learning.game.gamelogic.systems.RenderingSystem;

import java.util.Map;

public class GameScreenInputProcessor implements InputProcessor {

    private final LaserKittens laserKittens;
    private final Entity player;
    private final OrthographicCamera camera;
    private final World world;

    private boolean dragging;
    private int draggingPointer = -1;
    private Vector3 position = new Vector3();
    private Vector2 draggingStartedDiff = new Vector2();
    private MouseJoint mouseJoint = null;

    private final Body ground;


    public GameScreenInputProcessor(LaserKittens laserKittens, Entity player, OrthographicCamera camera, World world) {
        this.laserKittens = laserKittens;
        this.player = player;
        this.camera = camera;
        this.world = world;

        ground = BodyFactory.getBodyFactory(world).newCircleBody(new Vector2(0, 100), 0.1f, BodyDef.BodyType.StaticBody, true);
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
        float textureX = Mapper.transformComponent.get(player).position.x;
        float textureY = Mapper.transformComponent.get(player).position.y;
        float textureWidth = RenderingSystem.PixelsToMeters(Mapper.textureComponent.get(player).region.getRegionWidth())
                * Mapper.transformComponent.get(player).scale.x;
        float textureHeight = RenderingSystem.PixelsToMeters(Mapper.textureComponent.get(player).region.getRegionHeight())
                * Mapper.transformComponent.get(player).scale.y;

        float originX = textureWidth/2f;
        float originY = textureHeight/2f;


        Rectangle textureBounds = new Rectangle(textureX - originX,textureY - originY,textureWidth,textureHeight);
        return textureBounds.contains(position.x, position.y);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(position.set(screenX, screenY, 0));


        if (!clickInPlayerRegion()) {
            return false;
        }



        Body playerBody = Mapper.bodyComponent.get(player).body;
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
        dragging = false;
        draggingPointer = -1;
        if (mouseJoint != null) {
            world.destroyJoint(mouseJoint);
            mouseJoint = null;
        }
        Mapper.bodyComponent.get(player).body.setLinearVelocity(0, 0);
        return true;
    }

    Vector2 target = new Vector2();

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!dragging || pointer != draggingPointer) return false;

        camera.unproject(position.set(screenX, screenY, 0));
        position.x -= draggingStartedDiff.x;
        position.y -= draggingStartedDiff.y;
        mouseJoint.setTarget(target.set(position.x, position.y));

//        float playerX = Mapper.bodyComponent.get(player).body.getPosition().x;
//        float playerY = Mapper.bodyComponent.get(player).body.getPosition().y;
//        Mapper.bodyComponent.get(player).body.setTransform(position.x, position.y, 0);
//        Mapper.transformComponent.get(player).position.x = position.x;
//        Mapper.transformComponent.get(player).position.y = position.y;

        //camera.translate(position.x - playerX, position.y - playerY);

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