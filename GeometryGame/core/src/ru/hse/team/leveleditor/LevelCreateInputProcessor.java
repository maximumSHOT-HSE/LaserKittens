package ru.hse.team.leveleditor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private boolean dragging;
    private int draggingPointer = -1;

    public LevelCreateInputProcessor(LaserKittens laserKittens, LevelCreateScreen levelCreateScreen, OrthographicCamera camera) {
        this.laserKittens = laserKittens;
        this.levelCreateScreen = levelCreateScreen;
        this.camera = camera;
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
            levelCreateScreen.deleteOnPoint(position.x, position.y);
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

    public void chooseAnotherEntity(SimpleEntity.EntityType focusedType) {
        this.focusedType = focusedType;
        currentEntity = null;
    }

    public boolean isDragging() {
        return dragging;
    }
}