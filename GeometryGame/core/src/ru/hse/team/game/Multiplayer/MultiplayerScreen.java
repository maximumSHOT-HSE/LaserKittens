package ru.hse.team.game.Multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.game.Multiplayer.AppWarp.WarpController;
import ru.hse.team.game.Multiplayer.AppWarp.WarpListener;

public class MultiplayerScreen implements Screen, WarpListener {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage = new Stage(new ScreenViewport());

    private InputMultiplexer inputMultiplexer;

    public MultiplayerScreen(LaserKittens laserKittens) {
        this.laserKittens = laserKittens;
        background = new Background(laserKittens.getAssetManager().manager
                .get(KittensAssetManager.BLUE_BACKGROUND, Texture.class));
        InputProcessor inputProcessor = new MultiplayerScreenInputProcessor(laserKittens);
        inputMultiplexer = new InputMultiplexer(stage, inputProcessor);
    }

    @Override
    public void show() {
//        WarpController.getInstance().setWarpListener(this);
//        WarpController.getInstance().start(WarpController.generateRandomName());

        stage.clear(); // resets the stage listener
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.MAIN_MENU_SCREEN);
                }
                return true;
            }
        });
        Gdx.input.setInputProcessor(inputMultiplexer);

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

    // methods from WarpListener BEGIN

    @Override
    public void onWaitingStarted(String message) {
        System.out.println("MultiplayerScreen.onWaitingStarted: msg = " + message);
    }

    @Override
    public void onError(String message) {
        System.out.println("MultiplayerScreen.onError: msg = " + message);
//        this.msg = errorInConnection;
    }

    @Override
    public void onGameStarted(String message) {
        System.out.println("MultiplayerScreen.onGameStarted: msg = " + message);
    }

    @Override
    public void onGameFinished(WarpController.EndType endType, boolean isRemote) {
        System.out.println("MultiplayerScreen.onGameFinished: endType = " + endType);
    }

    @Override
    public void onGameUpdateReceived(String message) {
        System.out.println("MultiplayerScreen.onGameUpdateReceived: msg = " + message);
    }
}
