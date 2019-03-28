package com.example.learning.game.levels;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.MyAssetManager;
import com.example.learning.game.TestMoveLevelFactory;

public class TestMovePlayerLevel extends AbstractLevel {

    private TestMoveLevelFactory testMoveLevelFactory = new TestMoveLevelFactory();

    public TestMovePlayerLevel() {
        super("Test Move");
    }

    @Override
    public void createLevel(PooledEngine engine, MyAssetManager assetManager) {
        testMoveLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testMoveLevelFactory;
    }
}
