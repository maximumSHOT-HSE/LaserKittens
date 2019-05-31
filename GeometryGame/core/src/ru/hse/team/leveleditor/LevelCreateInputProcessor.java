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
 */
public class LevelCreateInputProcessor implements InputProcessor {

    private final LaserKittens laserKittens;
    private final LevelCreateScreen levelCreateScreen;
    private final OrthographicCamera camera;

    private SimpleEntity.EntityType focusedType;
    private SimpleEntity currentEntity;
    private float currentEntityScale = 1;
    private SimpleEntity player;

    private Tool tool;

    private boolean dragging;
    private int draggingPointer = -1;

    public LevelCreateInputProcessor(LaserKittens laserKittens, LevelCreateScreen levelCreateScreen,
                                     OrthographicCamera camera) {
        this.laserKittens = laserKittens;
        this.levelCreateScreen = levelCreateScreen;
        this.camera = camera;
    }

    public enum Tool {
        ERASER,
        CURSOR;
    }


    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.CHOOSE_LEVEL_SCREEN);
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

        if (dragging) return false;

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
            return false;
        }

        dragging = true;
        draggingPointer = pointer;

        if (currentEntity == null) {

            TextureRegion texture = levelCreateScreen.getTextureByType(focusedType);
            currentEntity = new SimpleEntity(position.x, position.y,
                    texture.getRegionWidth(), texture.getRegionHeight(),
                    0, focusedType);

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

        if (!dragging || draggingPointer != pointer) return false;

        dragging = false;
        draggingPointer = -1;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if (!dragging || draggingPointer != pointer) return false;

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

    public void rotateCurrentEntity(float delta) {
        if (currentEntity != null) {
            if (currentEntity.getType() != SimpleEntity.EntityType.PLAYER
                && currentEntity.getType() != SimpleEntity.EntityType.STAR) {
                currentEntity.setRotation(currentEntity.getRotation() + delta);
            }
        }
    }

    public void chooseAnotherEntity(SimpleEntity.EntityType focusedType) {
        this.focusedType = focusedType;
        currentEntity = null;
        tool = null;

        if (focusedType == SimpleEntity.EntityType.PLAYER && player != null) {
            currentEntity = player;
        }
    }

    public boolean isDragging() {
        return dragging;
    }

    public Tool getTool() {
        return tool;
    }

    public void changeTool(Tool tool) {
        this.tool = tool;
        focusedType = null;
        currentEntity = null;
    }

    public void zoomCurrentEntity(float scale) {
        if (currentEntity != null) {
            currentEntity.setSizeX(currentEntity.getSizeX() * scale / currentEntityScale);
            currentEntity.setSizeY(currentEntity.getSizeY() * scale / currentEntityScale);
            currentEntityScale = scale;
        }
    }
}