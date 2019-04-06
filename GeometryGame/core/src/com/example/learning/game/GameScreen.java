package com.example.learning.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.LaserKittens;
import com.example.learning.game.gamelogic.GameStatus;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;
import com.example.learning.game.gamelogic.systems.BulletSystem;
import com.example.learning.game.gamelogic.systems.GameStatusSystem;
import com.example.learning.game.gamelogic.systems.StateControlSystem;
import com.example.learning.game.gamelogic.systems.PhysicsDebugSystem;
import com.example.learning.game.gamelogic.systems.PhysicsSystem;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;
import com.example.learning.game.gameending.GameEndingScreen;

public class GameScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera;
    private AbstractLevel level;
    private GameStatus gameStatus = new GameStatus(this);
    private GameScreenInputProcessor inputProcessor;
    private PooledEngine engine;

    private RenderingSystem renderingSystem;
    private PhysicsSystem physicsSystem;
    private PhysicsDebugSystem physicsDebugSystem;
    private BulletSystem bulletSystem;
    private StateControlSystem stateControlSystem;
    private GameStatusSystem gameStatusSystem;

    public GameScreen(LaserKittens laserKittens, AbstractLevel abstractLevel) {
        level = abstractLevel;

        this.laserKittens = laserKittens;

        engine = new PooledEngine();
        abstractLevel.createLevel(engine, this.laserKittens.assetManager);
        AbstractLevelFactory levelFactory = abstractLevel.getFactory();
        World world = levelFactory.getWorld();
        world.setContactListener(new KittensContactListener());

        renderingSystem = new RenderingSystem(this.laserKittens.batch, this.laserKittens.shapeRenderer);
        camera = renderingSystem.getCamera();
        cameraMovingTo.set(camera.position);

        physicsSystem = new PhysicsSystem(world);
        physicsDebugSystem = new PhysicsDebugSystem(world, renderingSystem.getCamera());
        bulletSystem = new BulletSystem();
        stateControlSystem = new StateControlSystem(world, engine, gameStatus);
        gameStatusSystem = new GameStatusSystem(gameStatus);

        engine.addSystem(renderingSystem);
        engine.addSystem(physicsSystem);
        engine.addSystem(physicsDebugSystem);
        engine.addSystem(bulletSystem);
        engine.addSystem(stateControlSystem);
        engine.addSystem(gameStatusSystem);

        inputProcessor = new GameScreenInputProcessor(this.laserKittens, abstractLevel, camera);
    }

    public void endGame() {
        laserKittens.setScreen(new GameEndingScreen(laserKittens, level, this));
        dispose();
    }

    @Override
    public void show() {
        laserKittens.batch.setProjectionMatrix(camera.combined);
        Gdx.input.setInputProcessor(inputProcessor);
    }

    Vector3 cameraMovingTo = new Vector3();

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

    /** Moves camera with speed depended from distance exponentially */
    private void moveCamera(float delta) {
        delta = Math.max(delta, 0.1f); // when delta is near to zero problems occur
        final float speed = 2 * delta;
        final float ispeed = 1.0f-speed;

        Vector3 cameraPosition = new Vector3(camera.position);
        Entity player = level.getFactory().getPlayer();
        if (player != null) {
            TransformComponent playerTransform = Mapper.transformComponent.get(player);

            if (playerTransform == null) {
                return;
            }

            cameraMovingTo.set(playerTransform.position);
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
    public void render (float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);

        inputProcessor.touchDraggedExplicitly();
        moveCamera(delta);
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
        World world = level.getFactory().getWorld();
        for (Entity entity : engine.getEntities()) {
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
            if(bodyComponent != null) {
                world.destroyBody(bodyComponent.body);
            }
        }
        engine.removeAllEntities();
    }
}
