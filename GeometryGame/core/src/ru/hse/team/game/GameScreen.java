package ru.hse.team.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.physics.box2d.World;

import ru.hse.team.LaserKittens;
import ru.hse.team.game.Multiplayer.AbstractMultiplayerLevel;
import ru.hse.team.game.Multiplayer.AppWarp.WarpController;
import ru.hse.team.game.Multiplayer.AppWarp.WarpListener;
import ru.hse.team.game.gameending.GameEndingScreen;
import ru.hse.team.game.gamelogic.GameScreenInputProcessor;
import ru.hse.team.game.gamelogic.GestureProcessor;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.systems.BulletSystem;
import ru.hse.team.game.gamelogic.systems.PhysicsDebugSystem;
import ru.hse.team.game.gamelogic.systems.PhysicsSystem;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.gamelogic.systems.StateControlSystem;
import ru.hse.team.game.levels.AbstractLevel;

/**
 * Screen which shows current game states and game objects.
 * Objects related to game logic created when this screen
 *  is initialised and disposed when it is closed.
 */
public class GameScreen implements Screen {

    private final LaserKittens laserKittens;
    private AbstractLevel level;
    private PooledEngine engine;

    private GameScreenInputProcessor inputProcessor;
    private GestureProcessor gestureProcessor;
    private InputMultiplexer inputMultiplexer;

    private RenderingSystem renderingSystem;
    private PhysicsSystem physicsSystem;
    private PhysicsDebugSystem physicsDebugSystem;
    private BulletSystem bulletSystem;
    private StateControlSystem stateControlSystem;

    private WarpController warpController;

    public GameScreen(LaserKittens laserKittens, AbstractLevel abstractLevel) {
        level = abstractLevel;

        this.laserKittens = laserKittens;

        engine = new PooledEngine();
        abstractLevel.createLevel(engine, this.laserKittens.getAssetManager());

        renderingSystem = new RenderingSystem(abstractLevel, laserKittens);
        physicsSystem = new PhysicsSystem(abstractLevel);
        physicsDebugSystem = new PhysicsDebugSystem(abstractLevel.getWorld(), renderingSystem.getCamera());
        bulletSystem = new BulletSystem();
        stateControlSystem = new StateControlSystem(engine, abstractLevel);

        engine.addSystem(renderingSystem);
        engine.addSystem(physicsSystem);
        engine.addSystem(physicsDebugSystem);
        engine.addSystem(bulletSystem);
        engine.addSystem(stateControlSystem);

        inputProcessor = new GameScreenInputProcessor(
                this.laserKittens, abstractLevel, renderingSystem.getCamera());
        gestureProcessor = new GestureProcessor(renderingSystem, inputProcessor, abstractLevel);
        inputMultiplexer = new InputMultiplexer(
                new GestureDetector(gestureProcessor),
                inputProcessor);
    }

    public void endGame() {
        laserKittens.setScreen(new GameEndingScreen(laserKittens, level));
        dispose();
    }

    @Override
    public void show() {
        if (level instanceof AbstractMultiplayerLevel) {
            warpController = WarpController.getInstance();
            if (warpController != null) {
                System.out.println("SET WARP LISTENER ! level = " + level.getLevelName());
                warpController.setWarpListener((WarpListener) level);
            }
        }
        laserKittens.getBatch().setProjectionMatrix(renderingSystem.getCamera().combined);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);

        inputProcessor.touchDraggedExplicitly();
        inputProcessor.moveWithAccelerometer(delta);

        if (level.getGameStatus().readyToFinish()) {
            level.getGameStatus().stop();
            endGame();
        }
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
        World world = level.getWorld();
        for (Entity entity : engine.getEntities()) {
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
            if(bodyComponent != null && world != null && bodyComponent.body != null) {
                world.destroyBody(bodyComponent.body);
            }
        }
        engine.removeAllEntities();
        level.refreshGameStatus();
    }
}
