package ru.hse.team.game.gameending;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.database.statistics.LevelStatistics;
import ru.hse.team.game.GameScreen;
import ru.hse.team.game.Multiplayer.AbstractMultiplayerLevel;
import ru.hse.team.game.gamelogic.GameStatus;
import ru.hse.team.game.levels.AbstractLevel;


/**
 * Screen with general information about passed level
 *  and screens navigating buttons.
 *  Adds finished level result to LevelStatistics database
 */
public class GameEndingScreen implements Screen {
    private final LaserKittens laserKittens;
    private final AbstractLevel parentLevel;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Background background;
    private final Stage stage;

    public GameEndingScreen(LaserKittens laserKittens, AbstractLevel level) {
        this.laserKittens = laserKittens;
        this.parentLevel = level;
        this.background = new Background(
            laserKittens.getAssetManager().getImage(KittensAssetManager.Images.BLUE_BACKGROUND)
        );
        this.stage = new Stage(new ScreenViewport());
        addResultToDatabase(parentLevel.getGameStatus());
    }

    private void addResultToDatabase(GameStatus gameStatus) {
       laserKittens.getDatabase().statisticsDao()
               .insert(
                       new LevelStatistics(
                               parentLevel.getLevelName()
                               , gameStatus.timeGone()
                               , gameStatus.getCalendarDate()
                    )
                );
    }

    @Override
    public void show() {
        stage.clear();
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    laserKittens.changeScreen(LaserKittens.ScreenType.CHOOSE_LEVEL_SCREEN);
                }
                return true;
            }
        });
        Gdx.input.setInputProcessor(stage);

        new Menu(stage);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        laserKittens.getBatch().setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        laserKittens.getBatch().setProjectionMatrix(camera.combined);
        camera.update();

        laserKittens.getBatch().begin();
        background.draw(laserKittens.getBatch());
        laserKittens.getBatch().end();

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
        private final Skin skin = laserKittens.getAssetManager()
                .getSkin(KittensAssetManager.Skins.BLUE_SKIN);
        private final TextButton restartButton = new TextButton("Restart", skin);
        private final TextButton quitButton = new TextButton("Quit", skin);
        private final Table table = new Table();

        private final float screenWidth = Gdx.graphics.getWidth();
        private final float screenHeight = Gdx.graphics.getHeight();

        private final Label statusLabel;

        public Menu(Stage stage) {
            stage.addActor(table);
            table.setWidth(screenWidth * 0.6f);
            table.setHeight(screenHeight * 0.6f);
            table.center();

            restartButton.getLabel().setFontScale(LaserKittens.scaleToPreferredWidth());
            quitButton.getLabel().setFontScale(LaserKittens.scaleToPreferredWidth());
            statusLabel = new Label(GameStatus.getTimeStamp(parentLevel.getGameStatus().timeGone()), skin);
            statusLabel.setFontScale(LaserKittens.scaleToPreferredWidth() * 5f);

            table.setFillParent(true);
            table.add(restartButton).width(screenWidth * 0.6f).height(screenHeight * 0.2f);
            table.row().pad(5, 10, 5, 10);
            table.add(quitButton).width(screenWidth * 0.6f).height(screenHeight * 0.2f);
            table.row().pad(5, 10, 5, 10);
            table.add(statusLabel)
                .width(screenWidth * 0.6f)
                .height(screenHeight * 0.2f)
                .align(Align.center);
            statusLabel.setAlignment(Align.center);
            table.row().pad(5, 10, 5, 10);

            setListeners();
        }

        private void setListeners() {
            quitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (parentLevel instanceof AbstractMultiplayerLevel) {
                        laserKittens.changeScreen(LaserKittens.ScreenType.MAIN_MENU_SCREEN);
                    } else { // parentLevel instanceof AbstractLevel
                        laserKittens.changeScreen(LaserKittens.ScreenType.CHOOSE_LEVEL_SCREEN);
                    }
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
