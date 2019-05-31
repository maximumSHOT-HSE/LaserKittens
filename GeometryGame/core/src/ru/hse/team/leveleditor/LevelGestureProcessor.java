package ru.hse.team.leveleditor;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import ru.hse.team.LaserKittens;

public class LevelGestureProcessor implements GestureDetector.GestureListener {


    private float scale = 1;
    private Vector2 entityScale = new Vector2(1, 1);
    private Vector2 entityScaleBase = new Vector2(1, 1);
    private final LaserKittens laserKittens;
    private final LevelCreateInputProcessor levelCreateInputProcessor;
    private final OrthographicCamera camera;


    public LevelGestureProcessor(LaserKittens laserKittens, LevelCreateInputProcessor levelCreateInputProcessor,
                                 OrthographicCamera camera) {
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
        camera.position.set(x - 2 * deltaX, y + 2 * deltaY, 0);
        camera.update();
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        scale = camera.zoom;
        entityScale.set(entityScaleBase);
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

    private Vector2 distanceModule(Vector2 pointer1, Vector2 pointer2) {
        return new Vector2(Math.abs(pointer1.x - pointer2.x), Math.abs(pointer1.y - pointer2.y));
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        if (levelCreateInputProcessor.isDragging()) {
            Vector2 initialDistance = distanceModule(initialPointer1, initialPointer2);
            Vector2 distance = distanceModule(pointer1, pointer2);

            levelCreateInputProcessor.zoomCurrentEntity(
                    distance.x / initialDistance.x * entityScale.x,
                    distance.y / initialDistance.y * entityScale.y);
            entityScaleBase.set(
                    entityScale.x * distance.x / initialDistance.x,
                    entityScale.y * distance.y / initialDistance.y);
        }
        return false;
    }


    @Override
    public void pinchStop() {
        entityScale.set(entityScaleBase);
    }

    public void resetCurrentEntityScales() {
        entityScale.set(1, 1);
        entityScaleBase.set(1, 1);
    }
}

