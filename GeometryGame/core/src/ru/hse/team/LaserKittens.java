package ru.hse.team;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ru.hse.team.about.AboutScreen;
import ru.hse.team.database.AppDatabase;
import ru.hse.team.game.levels.ChooseLevelScreen;
import ru.hse.team.mainmenu.MainMenuScreen;
import ru.hse.team.settings.AppPreferences;
import ru.hse.team.settings.SettingsScreen;

public class LaserKittens extends Game {

    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public BitmapFont font;
    private final AppPreferences preferences = new AppPreferences();
    public final KittensAssetManager assetManager = new KittensAssetManager();
    public final AppDatabase database;

    public LaserKittens(AppDatabase database) {
        this.database = database;
    }


    public enum SCREEN_TYPE {
        MAIN_MENU_SCREEN,
        CHOOSE_LEVEL_SCREEN,
        SETTINGS_SCREEN,
        ABOUT_SCREEN;
    }

    private MainMenuScreen mainMenuScreen;
    private ChooseLevelScreen chooseLevelScreen;
    private SettingsScreen settingsScreen;
    private AboutScreen aboutScreen;

    public AppPreferences getPreferences() {
        return preferences;
    }

    /**
     * Change screen.
     * Creates it if it is not created
     */
    public void changeScreen(SCREEN_TYPE screen) {
        switch (screen) {
            case MAIN_MENU_SCREEN:
                if (mainMenuScreen == null) {
                    mainMenuScreen = new MainMenuScreen(this);
                }
                this.setScreen(mainMenuScreen);
                break;
            case CHOOSE_LEVEL_SCREEN:
                if (chooseLevelScreen == null) {
                    chooseLevelScreen = new ChooseLevelScreen(this);
                }
                this.setScreen(chooseLevelScreen);
                break;
            case SETTINGS_SCREEN:
                if (settingsScreen == null) {
                    settingsScreen = new SettingsScreen(this);
                }
                this.setScreen(settingsScreen);
                break;
            case ABOUT_SCREEN:
                if (aboutScreen == null) {
                    aboutScreen = new AboutScreen(this);
                }
                this.setScreen(aboutScreen);
                break;
        }
    }

    @Override
    public void create() {
        Gdx.input.setCatchBackKey(true);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        assetManager.loadEverything();
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
        shapeRenderer.dispose();
    }

}
