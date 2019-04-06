package com.example.learning.game.levels.TestLongCorridor;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestLongCorridorLevel extends AbstractLevel {
    private TestLongCorridorFactory testLongCorridorFactory = new TestLongCorridorFactory();

    public TestLongCorridorLevel() {
        super("Long Corridor");
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        testLongCorridorFactory.setLevelSize(1, 5);
        testLongCorridorFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testLongCorridorFactory;
    }
}
