package com.example.learning;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.learning.game.GameScreen;
import com.example.learning.settings.AppPreferences;
import com.example.learning.settings.SettingsScreen;

public class LaserKittens extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    private final AppPreferences preferences = new AppPreferences();
    public final MyAssetManager assetManager = new MyAssetManager();


    public enum SCREEN_TYPE {
        MAIN_MENU_SCREEN,
        GAME_SCREEN,
        SETTINGS_SCREEN
    }

    private MainMenuScreen mainMenuScreen;
    private GameScreen gameScreen;
    private SettingsScreen settingsScreen;

    public AppPreferences getPreferences() {
        return preferences;
    }

    public void changeScreen(SCREEN_TYPE screen) {
        switch (screen) {
            case MAIN_MENU_SCREEN:
                if (mainMenuScreen == null) {
                    mainMenuScreen = new MainMenuScreen(this);
                }
                this.setScreen(mainMenuScreen);
                break;
            case GAME_SCREEN:
                if (gameScreen == null) {
                    gameScreen = new GameScreen(this);
                }
                this.setScreen(gameScreen);
                break;
            case SETTINGS_SCREEN:
                if (settingsScreen == null) {
                    settingsScreen = new SettingsScreen(this);
                }
                this.setScreen(settingsScreen);
                break;
        }
        //should gameScreens be disposed?
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        assetManager.loadImages();
        assetManager.loadSkins();
        assetManager.manager.finishLoading();

        changeScreen(SCREEN_TYPE.MAIN_MENU_SCREEN);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
        assetManager.manager.dispose();
    }

}
