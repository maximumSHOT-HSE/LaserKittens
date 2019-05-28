package ru.hse.team.game.gameending;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.hse.team.Background;
import ru.hse.team.LaserKittens;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.database.statistics.LevelStatistics;
import ru.hse.team.game.GameScreen;
import ru.hse.team.game.gamelogic.GameStatus;
import ru.hse.team.game.levels.AbstractLevel;


/**
 * Screen with general information about passed level
 *  and screens navigating buttons.
 */
public class GameEndingScreen implements Screen {

    private LaserKittens laserKittens;
    private AbstractLevel parentLevel;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private InputMultiplexer inputMultiplexer;
    private Menu menu;
    private Stage stage;
    private GameStatus gameStatus;

    public GameEndingScreen(LaserKittens laserKittens, AbstractLevel level, GameStatus gameStatus) {
        this.laserKittens = laserKittens;
        this.parentLevel = level;
        this.background = new Background(laserKittens.assetManager.manager.get("blue-background.jpg", Texture.class));
        this.stage = new Stage(new ScreenViewport());
        InputProcessor inputProcessor = new GameEndingScreenInputProcessor(laserKittens);
        this.inputMultiplexer = new InputMultiplexer(stage, inputProcessor);
        this.gameStatus = gameStatus;

        addResultToDatabase(gameStatus);
    }


    private void addResultToDatabase(GameStatus gameStatus) {
        new Thread(() -> {
            laserKittens.getStatisticsDatabase().statisticsDao().insert(
                        new LevelStatistics(parentLevel.getName(),gameStatus.timeGone(), gameStatus.getStarsInLevel(), gameStatus.getCalendarDate()));
        }).start();
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(inputMultiplexer);

        menu = new Menu(stage);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        laserKittens.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        laserKittens.batch.setProjectionMatrix(camera.combined);
        camera.update();

        laserKittens.batch.begin();
        background.draw(laserKittens.batch, camera);
        laserKittens.batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        background.resizeClampToEdge();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {

    }

    private class Menu {
        private Skin skin = laserKittens.assetManager.manager.get(KittensAssetManager.skin);
        private TextButton restartButton = new TextButton("Restart", skin);
        private TextButton quitButton = new TextButton("Quit", skin);
        private Table table = new Table();

        private final float screenWidth = Gdx.graphics.getWidth();
        private final float screenHeight = Gdx.graphics.getHeight();

        private final Label statusLabel;

        public Menu(Stage stage) {
            stage.addActor(table);
            table.setWidth(0.6f * screenWidth);
            table.setHeight(0.6f * screenHeight);
            table.center();

            restartButton.getLabel().setFontScale(1f);
            quitButton.getLabel().setFontScale(1f);
            statusLabel = new Label(GameStatus.getTimeStamp(gameStatus.timeGone()), skin);
            statusLabel.setFontScale(5f);

            table.setFillParent(true);
            table.add(restartButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.row().pad(5, 10, 5, 10);
            table.add(quitButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.row().pad(5, 10, 5, 10);
            table.add(statusLabel).width(0.6f * screenWidth).height(0.2f * screenHeight).align(Align.center);
            statusLabel.setAlignment(Align.center);
            table.row().pad(5, 10, 5, 10);

            setListeners();
        }

        private void setListeners() {

            quitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.CHOOSE_LEVEL_SCREEN);
                }
            });

            restartButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.setScreen(new GameScreen(laserKittens, parentLevel));
                }
            });
        }
    }
}
