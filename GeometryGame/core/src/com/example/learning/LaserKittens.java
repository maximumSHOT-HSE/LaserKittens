package com.example.learning;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LaserKittens extends Game {

    SpriteBatch batch;
    BitmapFont font;
    private final AppPreferences preferences = new AppPreferences();
    public final MyAssetManager assetManager = new MyAssetManager();

    enum SCREEN_TYPE {
        MAIN_MENU_SCREEN,
        GAME_SCREEN,
        SETTINGS_SCREEN
    }

    private MainMenuScreen mainMenuScreen;
    private GameScreen gameScreen;
    private SettingsScreen settingsScreen;

    protected AppPreferences getPreferences() {
        return preferences;
    }

    void changeScreen(SCREEN_TYPE screen) {
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
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        assetManager.loadImages();
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
    }

}
