package com.example.learning.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.learning.Background;
import com.example.learning.LaserKittens;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.settings.SettingsScreenInputProcessor;

public class PopUpScreen implements Screen {

    private LaserKittens parent;
    private AbstractLevel parentLevel;
    private GameScreen gameScreen;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private InputMultiplexer inputMultiplexer;
    private Menu menu;
    private Stage stage;

    public PopUpScreen(LaserKittens parent, AbstractLevel level, GameScreen gameScreen) {
        this.parent = parent;
        this.parentLevel = level;
        this.background = new Background(parent.assetManager.manager.get("blue-background.jpg", Texture.class));
        this.stage = new Stage(new ScreenViewport());
        InputProcessor inputProcessor = new SettingsScreenInputProcessor(parent);
        this.inputMultiplexer = new InputMultiplexer(stage, inputProcessor);
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(inputMultiplexer);

        this.menu = new Menu(this.stage);

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
        private Skin skin = parent.assetManager.manager.get(KittensAssetManager.skin);
        private TextButton restartButton = new TextButton("Restart", skin);
        private TextButton quitButton = new TextButton("Quit", skin);
        private Table table = new Table();

        private final float screenWidth = Gdx.graphics.getWidth();
        private final float screenHeight = Gdx.graphics.getHeight();

        public Menu(Stage stage) {
            restartButton.getLabel().setFontScale(1f);
            restartButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.setScreen(new GameScreen(parent, parentLevel));
                }
            });
            quitButton.getLabel().setFontScale(1f);
            quitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.changeScreen(LaserKittens.SCREEN_TYPE.CHOOSE_LEVEL_SCREEN);
                }
            });

            table.setFillParent(true);
            table.add(restartButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.row().pad(5, 10, 5, 10);
            table.add(quitButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.row().pad(5, 10, 5, 10);
            table.setWidth(0.6f * screenWidth);
            table.setHeight(0.6f * screenHeight);
            table.center();
            stage.addActor(table);
        }
    }
}
