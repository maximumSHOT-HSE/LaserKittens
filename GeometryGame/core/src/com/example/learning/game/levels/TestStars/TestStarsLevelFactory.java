package com.example.learning.game.levels.TestStars;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.BodyFactory;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestStarsLevelFactory extends AbstractLevelFactory {

    private Entity player;

    public TestStarsLevelFactory(){
        world = new World(new Vector2(0,0), false);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Entity getPlayer() {
        return player;
    }



    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();
        createStar(10f, 10f, 3f);
        player = createPlayer(20f, 20f, 5f);
    }
}
