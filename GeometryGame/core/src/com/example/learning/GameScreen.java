package com.example.learning;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.example.learning.gamelogic.systems.CollisionSystem;
import com.example.learning.gamelogic.systems.PhysicsDebugSystem;
import com.example.learning.gamelogic.systems.PhysicsSystem;
import com.example.learning.gamelogic.systems.PlayerControlSystem;
import com.example.learning.gamelogic.systems.RenderingSystem;

public class GameScreen implements Screen {

    private final LaserKittens parent;
    private OrthographicCamera camera;
    private PooledEngine engine; // PooledEngine! reuse components. may cause problems
    //adding poolable interface may be needed somewhere
    private LevelFactory levelFactory;


    private InputMultiplexer inputMultiplexer;

    public GameScreen(LaserKittens geometryGame) {
        this.parent = geometryGame;

        engine = new PooledEngine();
        levelFactory = new LevelFactory(engine, parent.assetManager);
        levelFactory.world.setContactListener(new MyContactListener());

        // Create our new rendering system
        RenderingSystem renderingSystem = new RenderingSystem(parent.batch);
        camera = renderingSystem.getCamera();


        Gdx.input.setCatchBackKey(true);
        GestureDetector gestureDetector = new GestureDetector(new GameGestureListener(camera));
        InputProcessor inputProcessor = new com.example.learning.SettingsScreenInputProcessor(parent);
        inputMultiplexer = new InputMultiplexer(gestureDetector, inputProcessor);


        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(levelFactory.world));
        engine.addSystem(new PhysicsDebugSystem(levelFactory.world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem());

        levelFactory.createPlayer(10, 10);
        levelFactory.createPlayer(20, 20);
        levelFactory.createBackground();
    }

    @Override
    public void show() {

        parent.batch.setProjectionMatrix(camera.combined);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {

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
        levelFactory.world.dispose();
        engine.removeAllEntities();
        //probably bodies should be disposed somehow as well
    }
}
