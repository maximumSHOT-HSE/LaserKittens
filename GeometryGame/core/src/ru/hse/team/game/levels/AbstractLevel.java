package ru.hse.team.game.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.ContractProcessor;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.Multiplayer.AppWarp.WarpController;
import ru.hse.team.game.Multiplayer.MessageCreator;
import ru.hse.team.game.gamelogic.GameStatus;
import ru.hse.team.game.gamelogic.algorithms.AbstractGraph;

/**
 * Class for encapsulating logic and
 * data related with level
 * (creating, drawing and storing data, e.g. score, times, etc.).
 * */
abstract public class AbstractLevel {

    private static long RECHARGE_TIME = 500;
    private long lastShootTime = 0;

    /**
     * Name of the level.
     * User will see it in choose level screen
     */
    private String levelName;
    private AbstractGraph abstractGraph;
    private GameStatus gameStatus = new GameStatus();
    private Entity player;

    private int widthInScreens;
    private int heightInScreens;
    private World world = new World(new Vector2(0, 0), true);
    private BodyFactory bodyFactory = BodyFactory.getBodyFactory(world);

    public AbstractLevel(String levelName, int widthInScreens, int heightInScreens) {
        this.levelName = levelName;
        this.widthInScreens = widthInScreens;
        this.heightInScreens = heightInScreens;
        world.setContactListener(new ContractProcessor(this));
    }

    abstract public void createLevel(PooledEngine engine, KittensAssetManager assetManager);

    public String getLevelName() {
        return levelName;
    }

    abstract public AbstractLevelFactory getFactory();

    public void shoot(float x, float y) {

        long currentShootTime = System.currentTimeMillis();

        if (isMultiplayer() && currentShootTime - lastShootTime < RECHARGE_TIME) {
            return;
        }

        lastShootTime = currentShootTime;

        Vector3 playerPosition = Mapper.transformComponent.get(player).position;

        Vector2 direction = new Vector2(x - playerPosition.x, y - playerPosition.y);
        float length = (float) Math.sqrt(direction.x * direction.x + direction.y * direction.y);
        direction.set(direction.x / length, direction.y / length);
        float playerRadius = getPlayerRadius();

        Vector2 source =  new Vector2(playerPosition.x + playerRadius * direction.x,
                playerPosition.y + playerRadius * direction.y);
        int lifeTime = 10_000;

        getFactory().createLaser(source, direction, lifeTime);

        if (isMultiplayer()) {
            WarpController
                .getInstance()
                .sendGameUpdate(
                        MessageCreator.createShootMessage(
                                source, direction, lifeTime));
        }
    }

    public boolean isMultiplayer() {
        return false;
    }

    public AbstractGraph getAbstractGraph() {
        return abstractGraph;
    }

    public void setAbstractGraph(AbstractGraph abstractGraph) {
        this.abstractGraph = abstractGraph;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public float getPlayerRadius() {
        Body playerBody = Mapper.bodyComponent.get(player).body;
        return playerBody.getFixtureList().get(0).getShape().getRadius();
    }

    public Entity getPlayer() {
        return player;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public int getLevelWidthInScreens() {
        return widthInScreens;
    }

    public int getLevelHeightInScreens() {
        return heightInScreens;
    }

    public World getWorld() {
        return world;
    }

    public int getWidthInScreens() {
        return widthInScreens;
    }

    public int getHeightInScreens() {
        return heightInScreens;
    }

    public BodyFactory getBodyFactory() {
        return bodyFactory;
    }

    public void dispose() {
        world.dispose();
    }

    public void refreshGameStatus() {
        gameStatus = new GameStatus();
    }
}
