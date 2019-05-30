package ru.hse.team.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.database.statistics.LevelStatistics;
import ru.hse.team.game.GameScreen;
import ru.hse.team.game.gamelogic.GameStatus;
import ru.hse.team.game.levels.Quiz.QuizLevel;
import ru.hse.team.game.levels.RandomLabyrinth.RandomLabyrinthLevel;
import ru.hse.team.game.levels.TestBigLevel.TestBigLevel;
import ru.hse.team.game.levels.TestDoorsAndKeys.TestDoorsAndKeysLevel;
import ru.hse.team.game.levels.TestLongCorridor.TestLongCorridorLevel;
import ru.hse.team.game.levels.TestShooting.ShootingLevel;

public class ChooseLevelScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;

    private Stage stage;
    private Menu menu;

    private InputMultiplexer inputMultiplexer;

    private java.util.List<AbstractLevel> abstractLevels = new ArrayList<>();
    private int currentSection;

    private void fillLevels() {
        abstractLevels.add(new ShootingLevel());
        abstractLevels.add(new TestBigLevel());
        abstractLevels.add(new TestLongCorridorLevel());
        abstractLevels.add(new TestDoorsAndKeysLevel());
        abstractLevels.add(new RandomLabyrinthLevel(5, 5, 1, 3));
        abstractLevels.add(new QuizLevel());
    }

    public ChooseLevelScreen(LaserKittens laserKittens) {
        this.laserKittens = laserKittens;
        background = new Background(this.laserKittens.getAssetManager().manager.get(KittensAssetManager.BLUE_BACKGROUND, Texture.class));

        stage = new Stage(new ScreenViewport());

        fillLevels();
        currentSection = abstractLevels.size() + 3;

        menu = new Menu();
    }

    @Override
    public void show() {
        stage.clear();
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.MAIN_MENU_SCREEN);
                }
                return true;
            }
        });
        Gdx.input.setInputProcessor(stage);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        laserKittens.getBatch().setProjectionMatrix(camera.combined);

        menu = new Menu();
        menu.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        laserKittens.getBatch().begin();
        background.draw(laserKittens.getBatch());
        laserKittens.getBatch().end();

        menu.render();

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
        menu.saveState();
    }

    @Override
    public void dispose() {

    }

    private class Menu {
        private Skin skin = laserKittens.getAssetManager().manager.get(KittensAssetManager.SKIN);
        private SlidingPane slidingPane;
        private SlidingPane.DIRECTION direction = SlidingPane.DIRECTION.UP;
        private Texture naviActive = laserKittens.getAssetManager().manager.get(KittensAssetManager.LEVEL_INDICATOR_ACTIVE_PNG);
        private Texture naviPassive = laserKittens.getAssetManager().manager.get(KittensAssetManager.LEVEL_INDICATOR_PASSIVE_PNG);
        private final float screenWidth = Gdx.graphics.getWidth();
        private final float screenHeight = Gdx.graphics.getHeight();

        /**
         * Save current section id, which should be drawn
         * next time.
         * */
        public void saveState() {
            currentSection = slidingPane.currentSectionId;
        }

        public void render() {
            saveState();
            laserKittens.getBatch().begin();

            int levelsCount = abstractLevels.size() + 3;

            float h = naviActive.getHeight();
            float w = naviActive.getWidth();
            float x = 0.9f * screenWidth - 0.5f * w;
            float delta = 0.5f * h;
            float blockHeight = h * levelsCount + delta * (levelsCount - 1);
            float y = 0.5f * (screenHeight - blockHeight);

            for (int i = 1; i <= levelsCount; i++) {
                laserKittens.getBatch().draw(
                    i == currentSection ? naviActive : naviPassive,
                    x,
                    y + (i - 1) * (h + delta)
                );
            }

            laserKittens.getBatch().end();
        }

        private long getBestTime(String levelName) {
            long[] bestTime = new long[1];
            Thread queryThread = (new Thread(() -> {
                LevelStatistics statistics = laserKittens.getStatisticsDatabase().statisticsDao().getBestByLevelName(levelName);
                if (statistics != null) {
                    bestTime[0] = TimeUnit.NANOSECONDS.toMillis(statistics.timeNano);
                } else {
                    bestTime[0] = 1000_000_000;
                }
            }));
            queryThread.start();
            try {
                queryThread.join();
            } catch (InterruptedException exception) {
                Gdx.app.log("fail", "Database query interrupted");
            }
            return bestTime[0];
        }

        private Label getBestResult(String levelName) {
            Label[] label = new Label[1];
            Thread queryThread = (new Thread(() -> {
                LevelStatistics statistics = laserKittens.getStatisticsDatabase().statisticsDao().getBestByLevelName(levelName);
                if (statistics != null) {
                    label[0] = new Label(GameStatus.getTimeStamp(statistics.timeNano), skin);
                }
            }));
            queryThread.start();
            try {
                queryThread.join();
            } catch (InterruptedException exception) {
                Gdx.app.log("fail", "Database query interrupted");
            }
            return label[0];
        }

        private void addDumpLabels(Table table) {
            table.row();
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
        }

        private Table statisticsTable() {
            TextButton statisticsButton = new TextButton("Statistics", skin);
            statisticsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    currentSection = slidingPane.currentSectionId;
                    direction = slidingPane.direction;
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.STATISTICS_SCREEN);
                }
            });
            ImageButton scoreButton = new ImageButton(
                    new TextureRegionDrawable(laserKittens.getAssetManager().manager.get(KittensAssetManager.CUP, Texture.class)));
            scoreButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    currentSection = slidingPane.currentSectionId;
                    direction = slidingPane.direction;
                    laserKittens.getGoogleServices().submitScore(getBestTime("Quiz"));
                }
            });

            Table table = new Table();
            table.setWidth(0.6f * screenWidth);
            table.setHeight(0.6f * screenHeight);

            addDumpLabels(table);
            table.row();
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.add(statisticsButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.row();
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.add(scoreButton).width(Gdx.graphics.getWidth() * 0.2f).height(Gdx.graphics.getHeight() * 0.1f);
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
            return table;
        }

        private Table editorScreenTable() {
            TextButton statisticsButton = new TextButton("Level editor", skin);
            statisticsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    currentSection = slidingPane.currentSectionId;
                    direction = slidingPane.direction;
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.LEVEL_CREATE_SCREEN);
                }
            });

            Table table = new Table();
            table.setWidth(0.6f * screenWidth);
            table.setHeight(0.6f * screenHeight);

            addDumpLabels(table);
            table.row();
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.add(statisticsButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
            addDumpLabels(table);
            return table;
        }

        private Table savedLevelsTable() {
            TextButton savedLevelsButton = new TextButton("MyLevels", skin);
            savedLevelsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    currentSection = slidingPane.currentSectionId;
                    direction = slidingPane.direction;
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.SAVED_LEVELS_SCREEN);
                }
            });
            Table table = new Table();
            table.setWidth(0.6f * screenWidth);
            table.setHeight(0.6f * screenHeight);

            addDumpLabels(table);
            table.row();
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.add(savedLevelsButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.row();

            addDumpLabels(table);

            return table;
        }

        public void show(Stage stage) {
            // should be created here. Specific of implementation.
            slidingPane = new SlidingPane();
            slidingPane.addWidget(statisticsTable());

            for (AbstractLevel abstractLevel : abstractLevels) {
                TextButton levelButton = new TextButton(abstractLevel.getName(), skin);
                Label statusLabel = getBestResult(abstractLevel.getName());
                levelButton.getLabel().setFontScale(1.1f);
                levelButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        currentSection = slidingPane.currentSectionId;
                        direction = slidingPane.direction;
                        GameScreen gameScreen = new GameScreen(laserKittens, abstractLevel);
                        laserKittens.setScreen(gameScreen);
                    }
                });
                Table table = new Table();
                table.setWidth(0.6f * screenWidth);
                table.setHeight(0.6f * screenHeight);

                if (statusLabel != null) {
                    addDumpLabels(table);
                }

                addDumpLabels(table);
                table.row();
                table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.add(levelButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.row();
                if (statusLabel != null) {
                    statusLabel.setFontScale(5f);
                    table.add(statusLabel).width(0.6f * screenWidth).height(0.2f * screenHeight).align(Align.center).colspan(3);
                    statusLabel.setAlignment(Align.center);
                    table.row().pad(5, 10, 5, 10);
                }

                addDumpLabels(table);

                slidingPane.addWidget(table);
            }

            slidingPane.addWidget(editorScreenTable());
            slidingPane.addWidget(savedLevelsTable());
          
            slidingPane.setCurrentSection(currentSection, direction);
            stage.addActor(slidingPane);
        }
    }
}
