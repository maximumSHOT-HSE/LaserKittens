package ru.hse.team.leveleditor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ru.hse.team.LaserKittens;
import ru.hse.team.database.levels.SimpleEntity;

/**
 * Input processor for levelCreate screen.
 * Keeps currently chosen tool,
 *  {@code ERASER} for deleting objects on click
 *  {@code CURSOR} for focusing on objects on click
 * and currently focused entity.
 * Puts focused entity on position of touch.
 */
public class LevelCreateInputProcessor implements InputProcessor {
    private final LaserKittens laserKittens;
    private final LevelCreateScreen levelCreateScreen;
    private final OrthographicCamera camera;

    private SimpleEntity.EntityType focusedType;
    private SimpleEntity currentEntity;
    private final Vector2 currentEntityScale = new Vector2(1, 1);
    private SimpleEntity player;

    private Tool tool;

    private boolean dragging;
    private int draggingPointer = -1;

    public LevelCreateInputProcessor(
        LaserKittens laserKittens,
        LevelCreateScreen levelCreateScreen,
        OrthographicCamera camera
    ) {
        this.laserKittens = laserKittens;
        this.levelCreateScreen = levelCreateScreen;
        this.camera = camera;
    }

    /**
     * Tools for level creating.
     * {@code ERASER} for deleting objects on click
     * {@code CURSOR} for focusing on objects on click
     */
    public enum Tool {
        ERASER,
        CURSOR
    }

    /**
     * Returns to choose level screen on pressing {@code BACK} key event
     */
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            laserKittens.changeScreen(LaserKittens.ScreenType.CHOOSE_LEVEL_SCREEN);
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

    /**
     * Deals with touch event.
     * Ignores it if dragging focused entity now.
     * If there is no chosen entity type, uses current tool.
     * if there is current entity, puts it on position of touch,
     * otherwise creates new entity on position of touch, unless
     * current type is player and we already have player on map, then
     * player will only gets in focus and moves to the touch position
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (dragging) {
            return false;
        }

        Vector3 position = camera.unproject(new Vector3(screenX, screenY, 0));

        if (focusedType == null) {
            if (tool == null) {
                return false;
            }
            switch (tool) {
                case ERASER:
                    SimpleEntity eraseEntity = levelCreateScreen.entityOnPoint(position.x, position.y);
                    if (eraseEntity != null) {
                        levelCreateScreen.removeEntity(eraseEntity);
                        if (eraseEntity.getType() == SimpleEntity.EntityType.PLAYER) {
                            player = null;
                        }
                    }
                    break;
                case CURSOR:
                    SimpleEntity entity = levelCreateScreen.entityOnPoint(position.x, position.y);
                    if (entity != null) {
                        chooseAnotherEntity(entity.getType());
                        currentEntity = entity;
                    }
                    break;
            }
        }

        if (focusedType == null) {
            return false;
        }

        dragging = true;
        draggingPointer = pointer;

        if (currentEntity == null) {
            TextureRegion texture = levelCreateScreen.getTextureByType(focusedType);
            currentEntity = new SimpleEntity(
                position.x,
                position.y,
                texture.getRegionWidth() * LaserKittens.scaleToPreferredWidth(),
                texture.getRegionHeight() * LaserKittens.scaleToPreferredHeight(),
                0,
                focusedType
            );

            levelCreateScreen.addSimpleEntity(currentEntity);

            if (currentEntity.getType() == SimpleEntity.EntityType.PLAYER) {
                player = currentEntity;
            }

        } else {
            currentEntity.setPositionX(position.x);
            currentEntity.setPositionY(position.y);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!dragging || draggingPointer != pointer) {
            return false;
        }

        dragging = false;
        draggingPointer = -1;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!dragging || draggingPointer != pointer) {
            return false;
        }

        if (currentEntity == null) {
            return false;
        }

        Vector3 position = camera.unproject(new Vector3(screenX, screenY, 0));
        currentEntity.setPositionX(position.x);
        currentEntity.setPositionY(position.y);

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public SimpleEntity.EntityType getFocusedType() {
        return focusedType;
    }

    public SimpleEntity getCurrentEntity() {
        return currentEntity;
    }

    /**
     * Rotates current angle by delta degrees.
     * Ignores call if current entity does not exist or
     *  it is a player or a star.
     */
    public void rotateCurrentEntity(float delta) {
        if (currentEntity != null) {
            if (currentEntity.getType() != SimpleEntity.EntityType.PLAYER
                && currentEntity.getType() != SimpleEntity.EntityType.STAR) {
                currentEntity.setRotation(currentEntity.getRotation() + delta);
            }
        }
    }

    /**
     * Changes current entity type.
     * Forgets entity and tool chosen previously
     */
    public void chooseAnotherEntity(SimpleEntity.EntityType focusedType) {
        this.focusedType = focusedType;
        currentEntity = null;
        tool = null;

        if (focusedType == SimpleEntity.EntityType.PLAYER && player != null) {
            currentEntity = player;
        }

        currentEntityScale.set(1, 1);
        levelCreateScreen.getGestureProcessor().resetCurrentEntityScales();
    }

    public boolean isDragging() {
        return dragging;
    }

    public Tool getTool() {
        return tool;
    }

    /**
     * Changes current tool.
     * Forgets entity and tool chosen previously
     */
    public void changeTool(Tool tool) {
        this.tool = tool;
        focusedType = null;
        currentEntity = null;
    }

    /**
     * Zooms current entity.
     * If it is a player or a star, both coordinates are zoomed by
     *  argument vector length,
     *  otherwise coordinated zoomed independently
     */
    public void zoomCurrentEntity(float scaleX, float scaleY) {
        scaleX = Math.max(scaleX, 0.01f);
        scaleY = Math.max(scaleY, 0.01f);

        if (currentEntity != null) {
            if (currentEntity.getType() == SimpleEntity.EntityType.PLAYER ||
            currentEntity.getType() == SimpleEntity.EntityType.STAR) {
                float scale = (float) Math.sqrt(scaleX * scaleX + scaleY * scaleY);
                scaleX = scaleY = scale;
            }

            currentEntity.setSizeX(currentEntity.getSizeX() * scaleX / currentEntityScale.x);
            currentEntity.setSizeY(currentEntity.getSizeY() * scaleY / currentEntityScale.y);
            currentEntityScale.set(scaleX, scaleY);
        }
    }
}