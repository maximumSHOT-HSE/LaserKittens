package com.example.learning.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.learning.Background;
import com.example.learning.LaserKittens;
import com.example.learning.game.GameScreen;
import com.example.learning.game.levels.TestLaserLevel.TestLaserLevel;
import com.example.learning.game.levels.TestMovePlayerLevel.TestMovePlayerLevel;
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
    }

    public ChooseLevelScreen(LaserKittens laserKittens) {
        this.parent = laserKittens;
        background = new Background(parent.assetManager.manager.get("blue-background.jpg", Texture.class));

        stage = new Stage(new ScreenViewport());

        InputProcessor inputProcessor = new SettingsScreenInputProcessor(parent);
        inputMultiplexer = new InputMultiplexer(stage, inputProcessor);

        fillLevels();
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(inputMultiplexer);

        menu = new Menu(stage);

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

    }

    @Override
    public void dispose() {

    }

    private class Menu {
        private Table table = new Table();
        private Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        private SlidingPane slidingPane = new SlidingPane();

        public Menu(Stage stage) {
            for (AbstractLevel abstractLevel : abstractLevels) {
                TextButton levelButton = new TextButton(abstractLevel.getName(), skin);
                levelButton.getLabel().setFontScale(1f);
                levelButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        GameScreen gameScreen = new GameScreen(parent, abstractLevel);
                        parent.setScreen(gameScreen);
                    }
                });
                Table table = new Table();
                table.row();
//                table.add(new TextButton("1", skin).setStyle()).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.row();
                table.add(levelButton).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.row();
//                table.add(new TextButton("2", skin)).width(0.6f * Gdx.graphics.getWidth()).height(0.2f * Gdx.graphics.getHeight());
                table.row();
                table.setWidth(0.6f * Gdx.graphics.getWidth());
                table.setHeight(0.6f * Gdx.graphics.getHeight());
                slidingPane.addWidget(table);
            }

            System.out.println("finish");
            stage.addActor(slidingPane);
        }
    }
}
