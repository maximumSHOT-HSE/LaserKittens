package ru.hse.team.database.statistics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.game.gamelogic.GameStatus;
import ru.hse.team.settings.about.PagedScrollPane;

/**
 * Screen for showing statistics of all ever finished levels.
 * */
public class StatisticsScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage = new Stage(new ScreenViewport());
    private Menu menu;

    public StatisticsScreen(final LaserKittens laserKittens) {
        this.laserKittens = laserKittens;
        background = new Background(laserKittens.getAssetManager()
                .getImage(KittensAssetManager.Images.BLUE_BACKGROUND));
    }

    @Override
    public void show() {
        stage.clear();
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.CHOOSE_LEVEL_SCREEN);
                }
                return true;
            }
        });

        menu = new Menu(stage);
        Gdx.input.setInputProcessor(stage);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        laserKittens.getBatch().setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        laserKittens.getBatch().begin();
        background.draw(laserKittens.getBatch());
        laserKittens.getBatch().end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        background.resizeClampToEdge();
        stage.getViewport().update(width, height, true);
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
    public void dispose () {
        stage.dispose();
        background.dispose();
    }


    private class Menu {
        private Table table = new Table();
        private Skin skin = laserKittens.getAssetManager()
                .getSkin(KittensAssetManager.Skins.BLUE_SKIN);
        private Label titleLabel = new Label("Statistics",
                new Label.LabelStyle(laserKittens.getFont(), Color.WHITE));
        private final TextButton backButton = new TextButton("Back", skin);
        private List<List<Label>> listOfStatistics = new ArrayList<>();

        private List<Label> levelStatisticsToLabels(LevelStatistics levelStatistics) {
            List<Label> statisticsLabels = new ArrayList<>();
            statisticsLabels.add(new Label(levelStatistics.date, skin));
            statisticsLabels.add(new Label(levelStatistics.levelName, skin));
            statisticsLabels.add(new Label(
                    GameStatus.getTimeStamp(levelStatistics.timeNano), skin));
            return statisticsLabels;
        }

        private List<LevelStatistics> getAllLevels() {
            List<List<LevelStatistics>> allLevels = new ArrayList<>(1);
            Thread queryThread = new Thread(() ->
                    allLevels.add(laserKittens
                            .getDatabase().statisticsDao().getAll()));
            queryThread.start();
            try {
                queryThread.join();
            } catch (InterruptedException exception) {
                Gdx.app.log("fail", "Database query interrupted");
            }
            return allLevels.get(0);
        }

        public Menu(Stage stage) {
            for (LevelStatistics levelStatistics : getAllLevels()) {
                listOfStatistics.add(levelStatisticsToLabels(levelStatistics));
            }

            table.setFillParent(true);
            stage.addActor(table);

            titleLabel.setFontScale(5f * LaserKittens.scaleToPreferredWidth());

            table.row().pad(10, 10, 30, 10);
            table.add(titleLabel).colspan(2).expand();

            table.row().pad(20, 0, 20, 10);
            PagedScrollPane scroll = new PagedScrollPane(skin);
            scroll.setFlingTime(0.1f);
            scroll.setPageSpacing(25);

            Table information = new Table().pad(50);
            information.defaults().pad(10, 10, 10, 10);
            for (List<Label> statisticsGroup : listOfStatistics) {
                information.row();
                for (Label statistics : statisticsGroup) {
                    statistics.setFontScale(2 * LaserKittens.scaleToPreferredWidth());
                    information.add(statistics);
                }
            }
            scroll.addPage(information);

            table.add(scroll).expand().fill();

            table.row().pad(30, 10, 10, 10);
            table.add(backButton).
                    width(Gdx.graphics.getWidth() * 0.25f)
                    .height(Gdx.graphics.getHeight() * 0.1f).colspan(2).expand();

            setListeners();
        }

        private void setListeners() {

            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.CHOOSE_LEVEL_SCREEN);
                }
            });

        }

    }
}
