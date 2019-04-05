package com.example.learning.game.levels.TestMovePlayerLevel;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.BodyFactory;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestMoveLevelFactory extends AbstractLevelFactory {

    public TestMoveLevelFactory(){
        world = new World(new Vector2(0,0), false);
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
        focusedPlayer = createPlayer(5, 5, 5);
    }
}
