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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.example.learning.LaserKittens;
import com.example.learning.game.gamelogic.GameStatus;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;
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
    private GameStatus gameStatus = new GameStatus(this);
    //adding poolable interface may be needed somewhere

    private GameScreenInputProcessor inputProcessor;
    private PooledEngine engine; // PooledEngine! reuse components. may cause problems
    private World world;

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

        cameraMovingTo = new Vector3(camera.position);

        inputProcessor = new GameScreenInputProcessor(parent, abstractLevel, camera);
    }

    public void endGame() {
        parent.setScreen(new PopUpScreen(parent, level, this));
        dispose();
    }

    @Override
    public void show() {
        parent.batch.setProjectionMatrix(camera.combined);
        Gdx.input.setInputProcessor(inputProcessor);
    }

    Vector3 cameraMovingTo;
    Vector3 cameraPosition = new Vector3();

    private float distance2D(Vector3 a, Vector3 b) {
        return (float)Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    private void makeBordersForCamera() {
        float screenWidth = RenderingSystem.getScreenSizeInMeters().x;
        float screenHeight = RenderingSystem.getScreenSizeInMeters().y;

        float levelWidth = screenWidth * level.getFactory().getLevelWidthInScreens();
        float levelHeight = screenHeight * level.getFactory().getLevelHeightInScreens();

        cameraMovingTo.x = Math.max(cameraMovingTo.x, screenWidth / 2);
        cameraMovingTo.y = Math.max(cameraMovingTo.y, screenHeight / 2);
        cameraMovingTo.x = Math.min(cameraMovingTo.x, levelWidth - screenWidth / 2);
        cameraMovingTo.y = Math.min(cameraMovingTo.y, levelHeight - screenHeight / 2);
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);

        final float speed= 2 * delta;
        final float ispeed=1.0f-speed;

        inputProcessor.touchDraggedExplicitly();
        cameraPosition.set(camera.position);
        if (level.getFactory().getPlayer() != null) {
            if (Mapper.transformComponent.get(level.getFactory().getPlayer()) == null) {
                return;
            }

            Vector3 playerPosition = Mapper.transformComponent.get(level.getFactory().getPlayer()).position;
            if (playerPosition == null) {
                return;
            }
            cameraMovingTo.set(playerPosition);
            makeBordersForCamera();

            cameraPosition.scl(ispeed);
            cameraMovingTo.scl(speed);
            cameraPosition.add(cameraMovingTo);
            cameraMovingTo.scl(1f / speed);
            camera.position.set(cameraPosition);
        }
        camera.update();

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
