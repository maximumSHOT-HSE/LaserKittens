package ru.hse.team.game.levels;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.algorithms.AbstractGraph;

/**
 * Class for encapsulating logic and
 * data related with level
 * (creating, drawing and storing data, e.g. score, times, etc.).
 * */
abstract public class AbstractLevel {

    /**
     * Name of the level.
     * User will see it in choose level screen
     */
    private String name;
    private AbstractGraph abstractGraph;

    public AbstractGraph getAbstractGraph() {
        return abstractGraph;
    }

    public void setAbstractGraph(AbstractGraph abstractGraph) {
        this.abstractGraph = abstractGraph;
    }

    public AbstractLevel(String name) {
        this.name = name;
    }

    abstract public void createLevel(PooledEngine engine, KittensAssetManager assetManager);

    public String getName() {
        return name;
    }

    abstract public AbstractLevelFactory getFactory();

    public void shoot(float x, float y) {
        Vector3 playerPosition = Mapper.transformComponent.get(getFactory().getPlayer()).position;

        Vector2 direction = new Vector2(x - playerPosition.x, y - playerPosition.y);
        float length = (float) Math.sqrt(direction.x * direction.x + direction.y * direction.y);
        direction.set(direction.x / length, direction.y / length);
        float playerRadius = getFactory().getPlayerRadius();
        getFactory().createLaser(
                new Vector2(playerPosition.x + playerRadius * direction.x,
                        playerPosition.y + playerRadius * direction.y),
                direction, 10000
        );
    }

    public boolean isMultiplayer() {
        return false;
    }


}
