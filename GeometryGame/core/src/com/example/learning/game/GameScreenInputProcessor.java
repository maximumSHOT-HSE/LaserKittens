package com.example.learning.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.example.learning.LaserKittens;

public class GameScreenInputProcessor implements InputProcessor {

    private LaserKittens laserKittens;
    private Entity player;
    private OrthographicCamera camera;

    private boolean dragging = false;
    private Vector3 position = new Vector3();
    private Vector2 draggingStartedDiff = new Vector2();

    private static final float DRAGGING_DISTANCE = 5;

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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        camera.unproject(position.set(screenX, screenY, 0));
        position.x += draggingStartedDiff.x;
        position.y += draggingStartedDiff.y;

        if (Math.abs(position.x - Mapper.transformComponent.get(player).position.x) > DRAGGING_DISTANCE ||
                Math.abs(position.y - Mapper.transformComponent.get(player).position.y) > DRAGGING_DISTANCE) {
            return false;
        }

        dragging = true;
        camera.unproject(position.set(screenX, screenY, 0));
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