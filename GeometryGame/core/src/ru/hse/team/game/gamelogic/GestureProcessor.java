package ru.hse.team.game.gamelogic;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;

public class GestureProcessor implements GestureDetector.GestureListener {

    private float scale = 1;
    private RenderingSystem renderingSystem;
    private GameScreenInputProcessor gameScreenInputProcessor;
    private AbstractLevel abstractLevel;

    public GestureProcessor(
            RenderingSystem renderingSystem,
            GameScreenInputProcessor gameScreenInputProcessor,
            AbstractLevel abstractLevel) {
        this.renderingSystem = renderingSystem;
        this.gameScreenInputProcessor = gameScreenInputProcessor;
        this.abstractLevel = abstractLevel;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
//        if (count == 2 && renderingSystem.getCameraWaiting() <= 0) {
//            renderingSystem.getCamera().position.set(
//                    renderingSystem.getCamera().unproject(new Vector3(x, y, 0))
//            );
//            renderingSystem.getCamera().update();
//            renderingSystem.setCameraWaiting(10f);
//        }
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        abstractLevel.getAbstractGraph().setDrawGraph(true);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
//        renderingSystem.getCamera().position.set(
//                renderingSystem.getCamera().unproject(new Vector3(x, y, 0))
//        );
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (gameScreenInputProcessor.isDragging()) {
            return false;
        }
        x = renderingSystem.getCamera().position.x;
        y = renderingSystem.getCamera().position.y;
        renderingSystem.getCamera().position.set(
                x - deltaX / 10, y + deltaY / 10, 0
        );
        renderingSystem.getCamera().update();
        renderingSystem.setCameraWaiting((float) 1e9);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        scale = renderingSystem.getCamera().zoom;
        renderingSystem.setCameraWaiting(0);
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        renderingSystem.getCamera().zoom = scale * initialDistance / distance;
        if (renderingSystem.getCamera().zoom >= 3) {
            renderingSystem.getCamera().zoom = 3;
        }
        if (renderingSystem.getCamera().zoom < 1) {
            renderingSystem.getCamera().zoom = 1;
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
