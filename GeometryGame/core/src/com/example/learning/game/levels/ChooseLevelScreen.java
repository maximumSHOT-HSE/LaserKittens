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
import com.example.learning.KittensAssetManager;
import com.example.learning.game.GameScreen;
import com.example.learning.game.levels.TestBigLevel.TestBigLevel;
import com.example.learning.game.levels.TestDoorsAndKeys.TestDoorsAndKeysLevel;
import com.example.learning.game.levels.TestShooting.ShootingLevel;
import com.example.learning.game.levels.TestLongCorridor.TestLongCorridorLevel;
import com.example.learning.settings.SettingsScreenInputProcessor;

import java.util.ArrayList;

public class ChooseLevelScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;

    private Stage stage;
    private Menu menu;

    private InputMultiplexer inputMultiplexer;

    private java.util.List<AbstractLevel> abstractLevels = new ArrayList<>();

    private void fillLevels() {
        abstractLevels.add(new ShootingLevel());
        abstractLevels.add(new TestBigLevel());
        abstractLevels.add(new TestLongCorridorLevel());
        abstractLevels.add(new TestDoorsAndKeysLevel());
    }

    public ChooseLevelScreen(LaserKittens laserKittens) {
        this.laserKittens = laserKittens;
        background = new Background(this.laserKittens.assetManager.manager.get("blue-background.jpg", Texture.class));

        stage = new Stage(new ScreenViewport());

        InputProcessor inputProcessor = new SettingsScreenInputProcessor(this.laserKittens);
        inputMultiplexer = new InputMultiplexer(stage, inputProcessor);

        fillLevels();

        menu = new Menu();
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(inputMultiplexer);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        laserKittens.batch.setProjectionMatrix(camera.combined);

        menu.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        laserKittens.batch.begin();
        background.draw(laserKittens.batch, camera);
        laserKittens.batch.end();

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
        private Skin skin = laserKittens.assetManager.manager.get(KittensAssetManager.skin);
        private SlidingPane slidingPane;
        private int currentSection = abstractLevels.size();
        private SlidingPane.DIRECTION direction = SlidingPane.DIRECTION.UP;
        private Texture naviActive = laserKittens.assetManager.manager.get(KittensAssetManager.levelIndicatorActive);
        private Texture naviPassive = laserKittens.assetManager.manager.get(KittensAssetManager.levelIndicatorPassive);
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
            laserKittens.batch.begin();

            int levelsCount = abstractLevels.size();

            float h = naviActive.getHeight();
            float w = naviActive.getWidth();
            float x = 0.9f * screenWidth - 0.5f * w;
            float delta = 0.5f * h;
            float blockHeight = h * levelsCount + delta * (levelsCount - 1);
            float y = 0.5f * (screenHeight - blockHeight);

            for (int i = 1; i <= levelsCount; i++) {
                laserKittens.batch.draw(
                    i == currentSection ? naviActive : naviPassive,
                    x,
                    y + (i - 1) * (h + delta)
                );
            }

            laserKittens.batch.end();
        }

        public void show(Stage stage) {
            // should be created here. Specific of implementation.
            slidingPane = new SlidingPane();
            for (AbstractLevel abstractLevel : abstractLevels) {
                TextButton levelButton = new TextButton(abstractLevel.getName(), skin);
                levelButton.getLabel().setFontScale(1f);
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
                table.row();
                table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.row();
                table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.add(levelButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.row();
                table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.add(new Label("", skin)).width(0.6f * screenWidth).height(0.2f * screenHeight);
                table.setWidth(0.6f * screenWidth);
                table.setHeight(0.6f * screenHeight);
                slidingPane.addWidget(table);
            }
            slidingPane.setCurrentSection(currentSection, direction);
            stage.addActor(slidingPane);
        }
    }
}
