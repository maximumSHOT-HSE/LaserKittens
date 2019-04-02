package com.example.learning.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.learning.LaserKittens;
import com.example.learning.game.gamelogic.GameStatus;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.systems.BulletSystem;
import com.example.learning.game.gamelogic.systems.GameStatusSystem;
import com.example.learning.game.gamelogic.systems.GarbageCollectionSystem;
import com.example.learning.game.gamelogic.systems.LevelGenerationSystem;
import com.example.learning.game.gamelogic.systems.PhysicsDebugSystem;
import com.example.learning.game.gamelogic.systems.PhysicsSystem;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;

public class GameScreen implements Screen {

    private final LaserKittens parent;
    private OrthographicCamera camera;
    private AbstractLevel level;
    private GameStatus gameStatus = new GameStatus();
    //adding poolable interface may be needed somewhere

    private InputMultiplexer inputMultiplexer;
    private PooledEngine engine; // PooledEngine! reuse components. may cause problems
    private World world;
    private Stage stage;

    private RenderingSystem renderingSystem;
    private PhysicsSystem physicsSystem;
    private PhysicsDebugSystem physicsDebugSystem;
    private BulletSystem bulletSystem;
    private GarbageCollectionSystem garbageCollectionSystem;
    private LevelGenerationSystem levelGenerationSystem;
    private GameStatusSystem gameStatusSystem;

    public GameScreen(LaserKittens geometryGame, AbstractLevel abstractLevel) {
        level = abstractLevel;

        this.parent = geometryGame;

        engine = new PooledEngine();
        abstractLevel.createLevel(engine, parent.assetManager);
        AbstractLevelFactory levelFactory = abstractLevel.getFactory();
        world = levelFactory.getWorld();
        world.setContactListener(new MyContactListener());

        renderingSystem = new RenderingSystem(parent.batch, parent.shapeRenderer);
        camera = renderingSystem.getCamera();

        stage = new Stage(new ScreenViewport());

        physicsSystem = new PhysicsSystem(world);
        physicsDebugSystem = new PhysicsDebugSystem(world, renderingSystem.getCamera());
        bulletSystem = new BulletSystem();
        garbageCollectionSystem = new GarbageCollectionSystem(world, engine, gameStatus);
        levelGenerationSystem = new LevelGenerationSystem(abstractLevel.getFactory());
        gameStatusSystem = new GameStatusSystem(gameStatus);

        engine.addSystem(renderingSystem);
        engine.addSystem(physicsSystem);
        engine.addSystem(physicsDebugSystem);
        engine.addSystem(bulletSystem);
        engine.addSystem(garbageCollectionSystem);
        engine.addSystem(levelGenerationSystem);
        engine.addSystem(gameStatusSystem);

        GestureDetector gestureDetector = new GestureDetector(new GameGestureListener(camera));
        InputProcessor inputProcessor = new GameScreenInputProcessor(parent, abstractLevel, camera);
        inputMultiplexer = new InputMultiplexer(stage, gestureDetector, inputProcessor);
    }

    public void showEndGameDialog() {
        parent.setScreen(new PopUpScreen(parent, stage, level, this));
        dispose();
    }

    @Override
    public void show() {
        stage.clear();
        parent.batch.setProjectionMatrix(camera.combined);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);

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
        for (Entity entity : engine.getEntities()) {
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
            if(bodyComponent != null) {
                world.destroyBody(bodyComponent.body);
            }
        }
        engine.removeAllEntities();
    }
}
