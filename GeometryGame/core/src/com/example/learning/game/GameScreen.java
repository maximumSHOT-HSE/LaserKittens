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
import com.example.learning.LaserKittens;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.systems.BulletSystem;
import com.example.learning.game.gamelogic.systems.CollisionSystem;
import com.example.learning.game.gamelogic.systems.GarbageCollectionSystem;
import com.example.learning.game.gamelogic.systems.LevelGenerationSystem;
import com.example.learning.game.gamelogic.systems.PhysicsDebugSystem;
import com.example.learning.game.gamelogic.systems.PhysicsSystem;
import com.example.learning.game.gamelogic.systems.PlayerControlSystem;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;

public class GameScreen implements Screen {

    private final LaserKittens parent;
    private OrthographicCamera camera;
    private PooledEngine engine; // PooledEngine! reuse components. may cause problems
    //adding poolable interface may be needed somewhere

    private InputMultiplexer inputMultiplexer;
    private World world;

    public GameScreen(LaserKittens geometryGame, AbstractLevel abstractLevel) {
        this.parent = geometryGame;

        engine = new PooledEngine();
        abstractLevel.createLevel(engine, parent.assetManager);
        AbstractLevelFactory levelFactory = abstractLevel.getFactory();
        world = levelFactory.getWorld();
        world.setContactListener(new MyContactListener());

        // Create our new rendering system
        RenderingSystem renderingSystem = new RenderingSystem(parent.batch, parent.shapeRenderer);
        camera = renderingSystem.getCamera();

        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem());
        engine.addSystem(new BulletSystem());
        engine.addSystem(new GarbageCollectionSystem(world, engine));
        engine.addSystem(new LevelGenerationSystem(abstractLevel.getFactory()));

        GestureDetector gestureDetector = new GestureDetector(new GameGestureListener(camera));
        InputProcessor inputProcessor = new GameScreenInputProcessor(parent, abstractLevel, camera);
        inputMultiplexer = new InputMultiplexer(gestureDetector, inputProcessor);
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
        for (Entity entity : engine.getEntities()) {
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
            if(bodyComponent != null) {
                world.destroyBody(bodyComponent.body);
            }
        }
        engine.removeAllEntities();
    }
}
