package ru.hse.team;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

import ru.hse.team.about.AboutScreen;
import ru.hse.team.database.levels.LevelsDatabase;
import ru.hse.team.database.levels.SavedLevel;
import ru.hse.team.database.levels.SavedSimpleEntity;
import ru.hse.team.database.statistics.StatisticsDatabase;
import ru.hse.team.database.statistics.StatisticsScreen;
import ru.hse.team.game.Multiplayer.MultiplayerScreen;
import ru.hse.team.game.gamelogic.components.BulletComponent;
import ru.hse.team.game.levels.AbstractLevelFactory;
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

    private final StatisticsDatabase statisticsDatabase;
    private final LevelsDatabase savedLevels;
    private final GoogleServicesAction googleServices;

    public LaserKittens(StatisticsDatabase statisticsDatabase, LevelsDatabase savedLevels, GoogleServicesAction googleServicesAction) {
        super();
        this.statisticsDatabase = statisticsDatabase;
        this.savedLevels = savedLevels;
        this.googleServices = googleServicesAction;
    }

    public enum SCREEN_TYPE {
        MAIN_MENU_SCREEN,
        CHOOSE_LEVEL_SCREEN,
        MULTIPLAYER_SCREEN,
        SETTINGS_SCREEN,
        ABOUT_SCREEN,
        STATISTICS_SCREEN;
    }

    private MainMenuScreen mainMenuScreen;
    private ChooseLevelScreen chooseLevelScreen;
    private MultiplayerScreen multiplayerScreen;
    private SettingsScreen settingsScreen;
    private AboutScreen aboutScreen;
    private StatisticsScreen statisticsScreen;

    public AppPreferences getPreferences() {
        return preferences;
    }

    public StatisticsDatabase getStatisticsDatabase() {
        return statisticsDatabase;
    }

    public GoogleServicesAction getGoogleServices() {
        return googleServices;
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
            case MULTIPLAYER_SCREEN:
                if (multiplayerScreen == null) {
                    multiplayerScreen = new MultiplayerScreen(this);
                }
                this.setScreen(multiplayerScreen);
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
            case STATISTICS_SCREEN:
                if (statisticsScreen == null) {
                    statisticsScreen = new StatisticsScreen(this);
                }
                this.setScreen(statisticsScreen);
                break;
        }
    }

    @Override
    public void create() {
        Gdx.input.setCatchBackKey(true);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        assetManager.loadEverything();
        assetManager.manager.finishLoading();

        font = assetManager.manager.get(KittensAssetManager.font, BitmapFont.class);

        changeScreen(SCREEN_TYPE.MAIN_MENU_SCREEN);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        googleServices.signOut();
        batch.dispose();
        font.dispose();
        assetManager.manager.dispose();
        shapeRenderer.dispose();
    }
}
