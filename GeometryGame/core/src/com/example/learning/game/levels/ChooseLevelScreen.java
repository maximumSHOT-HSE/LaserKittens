package com.example.learning.game.levels;

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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.learning.Background;
import com.example.learning.LaserKittens;
import com.example.learning.game.GameScreen;
import com.example.learning.game.levels.TestLaserLevel.TestLaserLevel;
import com.example.learning.game.levels.TestMovePlayerLevel.TestMovePlayerLevel;
import com.example.learning.game.levels.TestShooting.ShootingLevel;
import com.example.learning.game.levels.TestStars.TestStarsLevel;
import com.example.learning.settings.SettingsScreenInputProcessor;

import java.util.ArrayList;

public class ChooseLevelScreen implements Screen {

    private final LaserKittens parent;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;

    private Stage stage;
    private Menu menu;

    private InputMultiplexer inputMultiplexer;

    private java.util.List<AbstractLevel> abstractLevels = new ArrayList<>();

    private void fillLevels() {
        abstractLevels.add(new TestLaserLevel());
        abstractLevels.add(new TestMovePlayerLevel());
        abstractLevels.add(new TestStarsLevel());
        abstractLevels.add(new ShootingLevel());
    }

    public ChooseLevelScreen(LaserKittens laserKittens) {
        this.parent = laserKittens;
        background = new Background(parent.assetManager.manager.get("blue-background.jpg", Texture.class));

        stage = new Stage(new ScreenViewport());

        InputProcessor inputProcessor = new SettingsScreenInputProcessor(parent);
        inputMultiplexer = new InputMultiplexer(stage, inputProcessor);

        fillLevels();

        menu = new Menu();
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(inputMultiplexer);

        menu.draw(stage);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        parent.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        parent.batch.begin();
        background.draw(parent.batch, camera);
        parent.batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
        menu.saveState();
    }

    @Override
    public void dispose() {

    }

    private class Menu {
        private Table table = new Table();
        private Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        private SlidingPane slidingPane;
        private int currentSection = abstractLevels.size();
        private SlidingPane.DIRECTION direction = SlidingPane.DIRECTION.UP;

        /**
         * Save current section id, which should be drawn
         * next time.
         * */
        public void saveState() {
            currentSection = slidingPane.currentSectionId;
        }

        /*
        * Table of small circles.
        * One of them indicates current level.
        * Others do not show anything.
        * */
        private Table createCircleLevelIndicator() {
            Table circles = new Table(skin);
            for (int i = 0; i < abstractLevels.size(); i++) {
                circles.add(new Label(i + " " + (i + 1 == currentSection ? "!" : "?"), skin));
                circles.row();
            }
            circles.setWidth(0.05f * Gdx.graphics.getWidth());
            circles.setHeight(0.2f * Gdx.graphics.getHeight());
            return circles;
        }

        public void draw(Stage stage) {
            slidingPane = new SlidingPane();
            for (AbstractLevel abstractLevel : abstractLevels) {
                TextButton levelButton = new TextButton(abstractLevel.getName(), skin);
                levelButton.getLabel().setFontScale(1f);
                levelButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        currentSection = slidingPane.currentSectionId;
                        direction = slidingPane.direction;
                        GameScreen gameScreen = new GameScreen(parent, abstractLevel);
                        parent.setScreen(gameScreen);
                    }
                });
                Table table = new Table();
                table.row();
                table.add(new Label("", skin)).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.add(new Label("", skin)).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.add(new Label("", skin)).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.row();
                table.add(new Label("", skin)).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.add(levelButton).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.row();
                table.add(createCircleLevelIndicator());
                table.row();
                table.add(new Label("", skin)).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.add(new Label("", skin)).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.add(new Label("", skin)).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.setWidth(0.6f * Gdx.graphics.getWidth());
                table.setHeight(0.6f * Gdx.graphics.getHeight());
                slidingPane.addWidget(table);
            }
            slidingPane.setCurrentSection(currentSection, direction);
            stage.addActor(slidingPane);
        }
    }
}
