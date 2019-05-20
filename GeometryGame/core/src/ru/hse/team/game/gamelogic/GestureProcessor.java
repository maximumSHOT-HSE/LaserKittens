package ru.hse.team.game.gamelogic;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ru.hse.team.game.gamelogic.systems.RenderingSystem;

public class GestureProcessor implements GestureDetector.GestureListener {

    private float scale = 1;
    private RenderingSystem renderingSystem;

    public GestureProcessor(
            RenderingSystem renderingSystem) {
        this.renderingSystem = renderingSystem;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (count >= 2) {
            renderingSystem.getCamera().position.set(
                    renderingSystem.getCamera().unproject(new Vector3(x, y, 0))
            );
            renderingSystem.getCamera().update();
            renderingSystem.setCameraWaiting(12f);
        }
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
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        scale = renderingSystem.getCamera().zoom;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        renderingSystem.getCamera().zoom = scale * initialDistance / distance;
        if (renderingSystem.getCamera().zoom >= 5) {
            renderingSystem.getCamera().zoom = 5;
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
