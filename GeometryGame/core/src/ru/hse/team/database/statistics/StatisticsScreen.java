package ru.hse.team.database.statistics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ru.hse.team.Background;
import ru.hse.team.LaserKittens;
import ru.hse.team.about.PagedScrollPane;
import ru.hse.team.database.LevelStatistics;
import ru.hse.team.game.gamelogic.GameStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Screen with general information about the game.
 * Information list contains
 *     External libraries, sounds, sceens
 */
public class StatisticsScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage;
    private Menu menu;

    private InputMultiplexer inputMultiplexer;

    public StatisticsScreen(final LaserKittens laserKittens) {
        this.laserKittens = laserKittens;

        background = new Background(laserKittens.assetManager.manager.get("blue-background.jpg", Texture.class));
        stage = new Stage(new ScreenViewport());

        InputProcessor inputProcessor = new StatisticsScreenInputProcessor(laserKittens);
        inputMultiplexer = new InputMultiplexer(stage, inputProcessor);
    }

    @Override
    public void show() {
        stage.clear();
        menu = new Menu(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        laserKittens.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update(); // good practise -- update camera one time per frame

        laserKittens.batch.begin();
        background.draw(laserKittens.batch, camera);
        laserKittens.batch.end();

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


    /**
     * Menu with clickable labels and scrollable plane.
     */
    private class Menu {
        private Table table = new Table();

        private Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        private Label titleLabel = new Label("Statistics", skin);
        private final TextButton backButton = new TextButton("Back", skin);

        List< List<Label> > listOfStatistics;

        private List<Label> levelStatisticsToLabels(LevelStatistics levelStatistics) {
            List<Label> statisticsLabels = new ArrayList<>();
            statisticsLabels.add(new Label(levelStatistics.date, skin));
            statisticsLabels.add(new Label(levelStatistics.levelName, skin));
            statisticsLabels.add(new Label(GameStatus.getTimeStamp(levelStatistics.timeNano), skin));
            return statisticsLabels;
        }

        private List<LevelStatistics> getAllLevels() {
            List<List<LevelStatistics>> allLevels = new ArrayList<>(1);
            Thread queryThread = new Thread(() ->{
                allLevels.add(laserKittens.getDatabase().statisticsDao().getAll());
            });
            queryThread.start();
            try {
                queryThread.join();
            } catch (InterruptedException exception) {
                Gdx.app.log("fail", "Database query interrupted");
            }
            return allLevels.get(0);
        }

        public Menu(Stage stage) {

            List<List<Label>> list = new ArrayList<>();
            for (LevelStatistics levelStatistics : getAllLevels()) {
                List<Label> labels = levelStatisticsToLabels(levelStatistics);
                list.add(labels);
            }
            listOfStatistics = list;

            table.setFillParent(true);
            stage.addActor(table);

            titleLabel.setFontScale(5f);

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
                    statistics.setFontScale(2);
                    information.add(statistics);
                }
            }
            scroll.addPage(information);

            table.add(scroll).expand().fill();

            table.row().pad(30, 10, 10, 10);
            table.add(backButton).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f).colspan(2).expand();

            setListeners();
        }

        private void setListeners() {

            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.MAIN_MENU_SCREEN);
                }
            });

        }

    }
}
