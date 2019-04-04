package com.example.learning.game.gameending;

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
import com.example.learning.game.GameScreen;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.settings.SettingsScreenInputProcessor;


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

    public GameEndingScreen(LaserKittens laserKittens, AbstractLevel level, GameScreen gameScreen) {
        this.laserKittens = laserKittens;
        this.parentLevel = level;
        this.background = new Background(laserKittens.assetManager.manager.get("blue-background.jpg", Texture.class));
        this.stage = new Stage(new ScreenViewport());
        InputProcessor inputProcessor = new SettingsScreenInputProcessor(laserKittens);
        this.inputMultiplexer = new InputMultiplexer(stage, inputProcessor);
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

        public Menu(Stage stage) {
            stage.addActor(table);

            restartButton.getLabel().setFontScale(1f);
            quitButton.getLabel().setFontScale(1f);

            table.setFillParent(true);
            table.add(restartButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.row().pad(5, 10, 5, 10);
            table.add(quitButton).width(0.6f * screenWidth).height(0.2f * screenHeight);
            table.row().pad(5, 10, 5, 10);
            table.setWidth(0.6f * screenWidth);
            table.setHeight(0.6f * screenHeight);
            table.center();

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
