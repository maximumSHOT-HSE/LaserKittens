package ru.hse.team.game.Multiplayer;

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

import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;

public class MultiplayerScreen implements Screen {

    private final LaserKittens parent;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage;
    private Menu menu;

    private InputMultiplexer inputMultiplexer;

    public MultiplayerScreen(LaserKittens parent) {
        this.parent = parent;
        background = new Background(parent.assetManager.manager.get("blue-background.jpg", Texture.class));
        stage = new Stage(new ScreenViewport());
        InputProcessor inputProcessor = new MultiplayerScreenInputProcessor(parent);
        inputMultiplexer = new InputMultiplexer(stage, inputProcessor);


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
        private Table table = new Table();
        private Skin skin = parent.assetManager.manager.get(KittensAssetManager.skin, Skin.class);

        final private TextButton backButton = new TextButton("Back", skin);

        public Menu(Stage stage) {
            table.setFillParent(true);
            //table.setDebug(true);
            stage.addActor(table);

            backButton.getLabel().setFontScale(3f);

            setListeners();

            table.row().pad(5, 10, 5, 10);
            table.add(backButton).width(Gdx.graphics.getWidth() * 0.65f).height(Gdx.graphics.getHeight() * 0.15f);
        }

        private void setListeners() {

            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.changeScreen(LaserKittens.SCREEN_TYPE.MAIN_MENU_SCREEN);
                }
            });

        }
    }
}
