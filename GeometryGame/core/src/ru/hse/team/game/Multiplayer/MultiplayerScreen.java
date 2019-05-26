package ru.hse.team.game.Multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.hse.team.Background;
import ru.hse.team.LaserKittens;
import ru.hse.team.game.Multiplayer.AppWarp.WarpController;
import ru.hse.team.game.Multiplayer.AppWarp.WarpListener;

public class MultiplayerScreen implements Screen, WarpListener {

    private final LaserKittens parent;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage;

    private InputMultiplexer inputMultiplexer;

    public MultiplayerScreen(LaserKittens parent) {
        this.parent = parent;
        background = new Background(parent.assetManager.manager.get("blue-background.jpg", Texture.class));
        stage = new Stage(new ScreenViewport());
        InputProcessor inputProcessor = new MultiplayerScreenInputProcessor(parent);
        inputMultiplexer = new InputMultiplexer(stage, inputProcessor);
        WarpController.getInstance().setWarpListener(this);
    }

    private final String[] tryingToConnect = {"Connecting","to AppWarp"};
    private final String[] waitForOtherUser = {"Waiting for","other user"};
    private final String[] errorInConnection = {"Error in","Connection", "Go Back"};
    private String[] msg = tryingToConnect;

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(inputMultiplexer);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        parent.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        System.out.println("-------------------");
        for (String word : msg) {
            System.out.println(word);
        }
        System.out.println("-------------------");

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

    // methods from WarpListener BEGIN

    @Override
    public void onWaitingStarted(String message) {

    }

    @Override
    public void onError(String message) {
        this.msg = errorInConnection;
    }

    @Override
    public void onGameStarted(String message) {
        System.out.println("GAME STARTED!!!!!!!!");
    }

    @Override
    public void onGameFinished(WarpController.EndType endType, boolean isRemote) {
        System.out.println("GAME FINISHED!!!!!!!");
    }

    @Override
    public void onGameUpdateReceived(String message) {
        this.msg = waitForOtherUser;
    }

    // methods from WarpListener END
}
