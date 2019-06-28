package ru.hse.team;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import ru.hse.team.database.GameDatabase;
import ru.hse.team.leveleditor.ChooseSavedLevelScreen;
import ru.hse.team.settings.about.AboutScreen;
import ru.hse.team.database.statistics.StatisticsScreen;
import ru.hse.team.game.Multiplayer.MultiplayerScreen;
import ru.hse.team.game.levels.ChooseLevelScreen;
import ru.hse.team.leveleditor.LevelCreateScreen;
import ru.hse.team.mainmenu.MainMenuScreen;
import ru.hse.team.settings.AppPreferences;
import ru.hse.team.settings.SettingsScreen;

/**
 * Main game class.
 * Used for changing screens and accessing common resources
 */
public class LaserKittens extends Game {

    public static final int PREFERRED_WIDTH = 1080;
    public static final int PREFERRED_HEIGHT = 1920;

    private final AndroidToolsHolder androidToolsHolder;
    private ToolsHolder toolsHolder;

    public LaserKittens(
        GameDatabase database,
        GoogleServicesAction googleServicesAction,
        AndroidActions androidActions
    ) {
        super();
        androidToolsHolder = new AndroidToolsHolder(database, googleServicesAction, androidActions);
    }

    public enum ScreenType {
        MAIN_MENU_SCREEN,
        CHOOSE_LEVEL_SCREEN,
        MULTIPLAYER_SCREEN,
        SETTINGS_SCREEN,
        ABOUT_SCREEN,
        STATISTICS_SCREEN,
        SAVED_LEVELS_SCREEN,
        LEVEL_CREATE_SCREEN
    }

    private MainMenuScreen mainMenuScreen;
    private ChooseLevelScreen chooseLevelScreen;
    private MultiplayerScreen multiplayerScreen;
    private SettingsScreen settingsScreen;
    private AboutScreen aboutScreen;
    private StatisticsScreen statisticsScreen;
    private LevelCreateScreen levelCreateScreen;
    private ChooseSavedLevelScreen savedLevelScreen;

    public AppPreferences getPreferences() {
        return toolsHolder.preferences;
    }

    public GoogleServicesAction getGoogleServices() {
        return androidToolsHolder.googleServices;
    }

    public GameDatabase getDatabase() {
        return androidToolsHolder.database;
    }

    /**
     * Change screen.
     * Creates it if it is not created
     */
    public void changeScreen(ScreenType screen) {
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
            case LEVEL_CREATE_SCREEN:
                if (levelCreateScreen == null) {
                    levelCreateScreen = new LevelCreateScreen(this);
                }
                this.setScreen(levelCreateScreen);
                break;
            case SAVED_LEVELS_SCREEN:
                if (savedLevelScreen == null) {
                    savedLevelScreen = new ChooseSavedLevelScreen(this);
                }
                this.setScreen(savedLevelScreen);
                break;
            default:
                throw new IllegalArgumentException("Screen Type not found!");
        }
    }

    @Override
    public void create() {
        Gdx.input.setCatchBackKey(true);
        System.out.println(Gdx.graphics.getWidth() + " " + Gdx.graphics.getHeight());

        toolsHolder = new ToolsHolder();

        changeScreen(ScreenType.MAIN_MENU_SCREEN);
    }

    @Override
    public void dispose() {
        super.dispose();
        toolsHolder.dispose();
        androidToolsHolder.dispose();
    }

    public GDXDialogs getDialogs() {
        return toolsHolder.dialogs;
    }

    public BitmapFont getFont() {
        return toolsHolder.font;
    }

    public SpriteBatch getBatch() {
        return toolsHolder.batch;
    }

    public ShapeRenderer getShapeRenderer() {
        return toolsHolder.shapeRenderer;
    }

    public KittensAssetManager getAssetManager() {
        return toolsHolder.assetManager;
    }

    public AndroidActions getAndroidActions() {
        return androidToolsHolder.androidActions;
    }

    public static float scaleToPreferredWidth() {
        return (float) Gdx.graphics.getWidth() / PREFERRED_WIDTH;
    }

    public static float scaleToPreferredHeight() {
        return (float) Gdx.graphics.getHeight() / PREFERRED_HEIGHT;
    }

    private class AndroidToolsHolder {
        private final GameDatabase database;
        private final GoogleServicesAction googleServices;
        private final AndroidActions androidActions;

        private AndroidToolsHolder(
                GameDatabase database
                , GoogleServicesAction googleServices
                , AndroidActions androidActions
        ) {
            this.database = database;
            this.googleServices = googleServices;
            this.androidActions = androidActions;
        }

        public void dispose() {
            googleServices.signOut();
        }
    }

    private class ToolsHolder {
        private final GDXDialogs dialogs;
        private final SpriteBatch batch;
        private final ShapeRenderer shapeRenderer;
        private final AppPreferences preferences;
        private final KittensAssetManager assetManager;
        private final BitmapFont font;

        private ToolsHolder() {
            dialogs = GDXDialogsSystem.install();
            batch = new SpriteBatch();
            shapeRenderer = new ShapeRenderer();
            preferences = new AppPreferences();
            assetManager = new KittensAssetManager();

            getAssetManager().loadEverything();
            font = getAssetManager().getFont(KittensAssetManager.Fonts.FONT);
        }

        public void dispose() {
            batch.dispose();
            font.dispose();
            assetManager.dispose();
            shapeRenderer.dispose();
        }

    }
}
