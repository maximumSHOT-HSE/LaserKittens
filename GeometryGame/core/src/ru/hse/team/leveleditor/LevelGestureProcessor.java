package ru.hse.team.leveleditor;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import ru.hse.team.LaserKittens;

public class LevelGestureProcessor implements GestureDetector.GestureListener {

    private float scale = 1;
    private final LaserKittens laserKittens;
    private final LevelCreateInputProcessor levelCreateInputProcessor;
    private final OrthographicCamera camera;


    public LevelGestureProcessor(LaserKittens laserKittens, LevelCreateInputProcessor levelCreateInputProcessor, OrthographicCamera camera) {
        this.laserKittens = laserKittens;
        this.levelCreateInputProcessor = levelCreateInputProcessor;
        this.camera = camera;
    }


    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (levelCreateInputProcessor.isDragging()) {
            return false;
        }
        x = camera.position.x;
        y = camera.position.y;
        camera.position.set(x - deltaX, y + deltaY, 0);
        camera.update();
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        scale = camera.zoom;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (levelCreateInputProcessor.isDragging()) {
            return false;
        }
        camera.zoom = scale * initialDistance / distance;
        if (camera.zoom >= 3) {
            camera.zoom = 3;
        }
        if (camera.zoom < 1) {
            camera.zoom = 1;
        }
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }


    @Override
    public void pinchStop() {

    }
}

