package com.example.learning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.SkinLoader;
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

public class MainMenuScreen implements Screen {

    private final LaserKittens parent;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage;
    private Menu menu;

    public MainMenuScreen(final LaserKittens laserKittens) {
        this.parent = laserKittens;
        background = new Background(parent.assetManager.manager.get("blue-background.jpg", Texture.class));

        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);

        menu = new Menu(stage);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        parent.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update(); // good practise -- update camera one time per frame

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
    public void dispose () {
        stage.dispose();
        background.dispose();
    }

    private class Menu {
        private Table menuTable = new Table();
       // private Skin skin = parent.assetManager.manager.get("skin/glassy-ui.json", Skin.class);
       private Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        private TextButton play = new TextButton("Play", skin);
        private TextButton settings = new TextButton("Settings", skin);
        private TextButton exit = new TextButton("Exit", skin);

        public Menu(Stage stage) {
            // creating menu table (actor) for buttons
            menuTable.setFillParent(true);
         //   menuTable.setDebug(true);
            stage.addActor(menuTable);

            play.getLabel().setFontScale(2f);
            settings.getLabel().setFontScale(2f);
            exit.getLabel().setFontScale(2f);

            setLinsteners();

            menuTable.add(play).width(Gdx.graphics.getWidth() * 0.65f).height(Gdx.graphics.getHeight() * 0.15f);
            menuTable.row().pad(10, 0, 10, 0);
            menuTable.add(settings).width(Gdx.graphics.getWidth() * 0.65f).height(Gdx.graphics.getHeight() * 0.15f).row();
            menuTable.add(exit).width(Gdx.graphics.getWidth() * 0.65f).height(Gdx.graphics.getHeight() * 0.15f).row();
        }

        private void setLinsteners() {
            play.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.changeScreen(LaserKittens.SCREEN_TYPE.GAME_SCREEN);
                }
            });

            settings.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.changeScreen(LaserKittens.SCREEN_TYPE.SETTINGS_SCREEN);
                }
            });

            exit.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });
        }
    }
}
