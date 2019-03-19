package com.example.learning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {

    private final LaserKittens geometryGame;
    private OrthographicCamera camera = new OrthographicCamera(24, 32);
    private Model model;
    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
    private Background background = new Background("blue-background.jpg");
    private Stage stage;
    private GestureDetector controller = new GestureDetector(new CatGestureListener(camera));

    public GameScreen(LaserKittens geometryGame) {
        this.geometryGame = geometryGame;
        stage = new Stage(new ScreenViewport());

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        model = new Model(controller);
    }

    @Override
    public void show() {


        //Gdx.input.setInputProcessor(stage);
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render (float delta) {
        model.step(delta);
        /*
        * Clear screen with dark blue color
        * */
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /*
        * Send message to camera about need of updating of matrices
        * */
        camera.update(); // good practise -- update camera one time per frame

        geometryGame.batch.begin();
            background.draw(geometryGame.batch, camera);
        geometryGame.batch.end();

        debugRenderer.render(model.world, camera.combined);

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
        stage.dispose();
        background.dispose();
    }
}
