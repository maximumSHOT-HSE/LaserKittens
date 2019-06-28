package ru.hse.team.game.gamelogic;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;

/**
 * Class which processes gestures on game screen.
 */
public class GestureProcessor implements GestureDetector.GestureListener {
    private float scale = 1;
    private final RenderingSystem renderingSystem;
    private final GameScreenInputProcessor gameScreenInputProcessor;
    private final AbstractLevel abstractLevel;

    public GestureProcessor(
        RenderingSystem renderingSystem,
        GameScreenInputProcessor gameScreenInputProcessor,
        AbstractLevel abstractLevel
    ) {
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
        return false;
    }

    /**
     * Shows level graph.
     */
    @Override
    public boolean longPress(float x, float y) {
        if (abstractLevel.getAbstractGraph() != null) {
            abstractLevel.getAbstractGraph().setDrawGraph(true);
        }
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    /**
     * Moves camera if user currently not dragging a player.
     * Movement made in opposite direction of pointer movement
     */
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

    /**
     * Zooms camera.
     * Zoom is bounded and if it get's too small it is increased to minimal value
     * and if it get't too big it is decreased to maximum value
     */
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
