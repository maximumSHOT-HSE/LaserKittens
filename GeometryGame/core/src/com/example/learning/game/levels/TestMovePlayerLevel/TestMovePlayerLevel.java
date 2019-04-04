package com.example.learning.game.levels.TestMovePlayerLevel;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestMovePlayerLevel extends AbstractLevel {

    private TestMoveLevelFactory testMoveLevelFactory = new TestMoveLevelFactory();

    public TestMovePlayerLevel() {
        super("Test Move");
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        testMoveLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testMoveLevelFactory;
    }
}
