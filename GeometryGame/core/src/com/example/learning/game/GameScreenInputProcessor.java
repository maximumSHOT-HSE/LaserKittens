package com.example.learning.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.example.learning.LaserKittens;
import com.example.learning.game.gamelogic.components.PlayerComponent;
import com.example.learning.game.gamelogic.systems.RenderingSystem;

import java.util.Map;

public class GameScreenInputProcessor implements InputProcessor {

    private LaserKittens laserKittens;
    private Entity player;
    private OrthographicCamera camera;

    private boolean dragging = false;
    private Vector3 position = new Vector3();
    private Vector2 draggingStartedDiff = new Vector2();


    public GameScreenInputProcessor(LaserKittens laserKittens, Entity player, OrthographicCamera camera) {
        this.laserKittens = laserKittens;
        this.player = player;
        this.camera = camera;
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
        System.out.println(textureBounds.x + " " + textureBounds.y + " ! " + textureBounds.width + " " + textureBounds.height);
        System.out.println(position.x + " " + position.y);
        return textureBounds.contains(position.x, position.y);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(position.set(screenX, screenY, 0));


        if (!clickInPlayerRegion()) return false;

        dragging = true;
        float playerX = Mapper.bodyComponent.get(player).body.getPosition().x;
        float playerY = Mapper.bodyComponent.get(player).body.getPosition().y;
        draggingStartedDiff.set(position.x - playerX, position.y - playerY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        camera.unproject(position.set(screenX, screenY, 0));
        dragging = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!dragging) return false;
        camera.unproject(position.set(screenX, screenY, 0));
        position.x -= draggingStartedDiff.x;
        position.y -= draggingStartedDiff.y;


        float playerX = Mapper.bodyComponent.get(player).body.getPosition().x;
        float playerY = Mapper.bodyComponent.get(player).body.getPosition().y;
        Mapper.bodyComponent.get(player).body.setTransform(position.x, position.y, 0);
        Mapper.transformComponent.get(player).position.x = position.x;
        Mapper.transformComponent.get(player).position.y = position.y;

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