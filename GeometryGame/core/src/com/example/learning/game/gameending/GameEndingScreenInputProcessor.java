package com.example.learning.game.gameending;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.example.learning.LaserKittens;

/**
 * Input processor for gameEnding screen.
 */
public class GameEndingScreenInputProcessor implements InputProcessor {

    LaserKittens laserKittens;

    public GameEndingScreenInputProcessor(LaserKittens laserKittens) {
        this.laserKittens = laserKittens;
    }

    /**
     * Catches key back and opens CHOOSE_LEVEL_SCREEN
     */
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
}
