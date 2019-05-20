package ru.hse.team.game.levels.Quiz;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class QuizLevelFactory extends AbstractLevelFactory {

    public QuizLevelFactory() {
        world = new World(new Vector2(0, 0), true);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();

        // plug player
        focusedPlayer = createPlayer(
                1e9f, 1e9f, 1
        );

        // plug star
        createStar(-1e9f, 1e9f, 1);
    }
}
