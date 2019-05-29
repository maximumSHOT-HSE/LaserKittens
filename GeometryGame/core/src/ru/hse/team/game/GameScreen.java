package ru.hse.team.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

import ru.hse.team.LaserKittens;
import ru.hse.team.game.gameending.GameEndingScreen;
import ru.hse.team.game.gamelogic.GameScreenInputProcessor;
import ru.hse.team.game.gamelogic.GameStatus;
import ru.hse.team.game.gamelogic.GestureProcessor;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.TransformComponent;
import ru.hse.team.game.gamelogic.systems.BulletSystem;
import ru.hse.team.game.gamelogic.systems.PhysicsDebugSystem;
import ru.hse.team.game.gamelogic.systems.PhysicsSystem;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.gamelogic.systems.StateControlSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class GameScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera;
    private AbstractLevel level;
    private GameStatus gameStatus;
    private GameScreenInputProcessor inputProcessor;
    private GestureProcessor gestureProcessor;
    private InputMultiplexer inputMultiplexer;
    private PooledEngine engine;

    private RenderingSystem renderingSystem;
    private PhysicsSystem physicsSystem;
    private PhysicsDebugSystem physicsDebugSystem;
    private BulletSystem bulletSystem;
    private StateControlSystem stateControlSystem;

    private Vector3 cameraMovingTo = new Vector3();

    public GameScreen(LaserKittens laserKittens, AbstractLevel abstractLevel) {
        level = abstractLevel;
        gameStatus = new GameStatus(this, laserKittens.font, laserKittens.batch);

        this.laserKittens = laserKittens;

        engine = new PooledEngine();
        abstractLevel.createLevel(engine, this.laserKittens.assetManager);
        AbstractLevelFactory levelFactory = abstractLevel.getFactory();
        World world = levelFactory.getWorld();
        world.setContactListener(new ContractProcessor(abstractLevel));

        renderingSystem = new RenderingSystem(this.laserKittens.batch, this.laserKittens.shapeRenderer, abstractLevel, laserKittens);
        camera = renderingSystem.getCamera();
        camera.zoom = 1.5f;
        cameraMovingTo.set(camera.position);

        physicsSystem = new PhysicsSystem(world, abstractLevel);
        physicsDebugSystem = new PhysicsDebugSystem(world, renderingSystem.getCamera());
        bulletSystem = new BulletSystem();
        stateControlSystem = new StateControlSystem(world, engine, gameStatus, abstractLevel);

        engine.addSystem(renderingSystem);
        engine.addSystem(physicsSystem);
        engine.addSystem(physicsDebugSystem);
        engine.addSystem(bulletSystem);
        engine.addSystem(stateControlSystem);

        inputProcessor = new GameScreenInputProcessor(this.laserKittens, abstractLevel, camera, gameStatus);
        gestureProcessor = new GestureProcessor(renderingSystem, inputProcessor, abstractLevel);
        inputMultiplexer = new InputMultiplexer(
                new GestureDetector(gestureProcessor),
                inputProcessor);
    }

    public void endGame() {
        laserKittens.setScreen(new GameEndingScreen(laserKittens, level, gameStatus));
        dispose();
    }

    @Override
    public void show() {
        laserKittens.batch.setProjectionMatrix(camera.combined);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void makeBordersForCamera(Vector3 position) {
        float screenWidth = RenderingSystem.getScreenSizeInMeters().x;
        float screenHeight = RenderingSystem.getScreenSizeInMeters().y;

        float levelWidth = screenWidth * level.getFactory().getLevelWidthInScreens();
        float levelHeight = screenHeight * level.getFactory().getLevelHeightInScreens();

        position.x = Math.max(position.x, screenWidth * camera.zoom / 2 - screenWidth / (2 * camera.zoom));
        position.y = Math.max(position.y, screenHeight - camera.zoom * camera.zoom / 2 - screenHeight / (2 * camera.zoom));
        position.x = Math.min(position.x, levelWidth - screenWidth * camera.zoom / 2 + screenWidth / (2 * camera.zoom));
        position.y = Math.min(position.y, levelHeight - screenHeight * camera.zoom / 2 + screenHeight / (2 * camera.zoom));
    }

    /** Moves camera with speed depended from distance exponentially */
    private void moveCamera(float delta) {

        delta = Math.max(delta, 0.1f); // when delta is near to zero problems occur
        final float speed = 2 * delta;
        final float ispeed = 1.0f-speed;

        Vector3 cameraPosition = new Vector3(camera.position);
        Entity player = level.getFactory().getPlayer();

        renderingSystem.decreaseCameraWaitingTime(delta);

        if (player != null && renderingSystem.getCameraWaiting() == 0) {
            TransformComponent playerTransform = Mapper.transformComponent.get(player);

            if (playerTransform == null) {
                return;
            }

            cameraMovingTo.set(playerTransform.position);
            makeBordersForCamera(cameraMovingTo);

            cameraPosition.scl(ispeed);
            cameraMovingTo.scl(speed);
            cameraPosition.add(cameraMovingTo);
            cameraMovingTo.scl(1f / speed);
            camera.position.set(cameraPosition);
        }

        makeBordersForCamera(camera.position);

        camera.update();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);

        inputProcessor.touchDraggedExplicitly();
        inputProcessor.moveWithAccelerometer(delta);

        moveCamera(delta);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public LaserKittens getGame() {
        return laserKittens;
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
                if (world != null) {
                    if (bodyComponent.body != null) {
                        world.destroyBody(bodyComponent.body);
                    }
                }
            }
        }
        engine.removeAllEntities();
        if (world != null) {
            world.dispose();
        }
    }
}
