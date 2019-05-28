package ru.hse.team.leveleditor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import ru.hse.team.LaserKittens;
import ru.hse.team.database.levels.SimpleEntity;

/**
 * Input processor for levelCreate screen.
 */
public class LevelCreateInputProcessor implements InputProcessor {

    private final LaserKittens laserKittens;
    private SimpleEntity.EntityType focusedType = null;
    private final LevelCreateScreen levelCreateScreen;
    private final OrthographicCamera camera;

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
//        if (focusedType == null) {
//            return false;
//        }
        Vector3 position = camera.unproject(new Vector3(screenX, screenY, 0));
        levelCreateScreen.addSimpleEntity(new SimpleEntity(position.x, position.y, 100, 100, 0, SimpleEntity.EntityType.STAR));

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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

    public void setFocusedType(SimpleEntity.EntityType focusedType) {
        this.focusedType = focusedType;
    }
}