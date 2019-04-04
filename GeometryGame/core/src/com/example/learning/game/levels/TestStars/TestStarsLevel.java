package com.example.learning.game.levels.TestStars;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestStarsLevel extends AbstractLevel {
    private TestStarsLevelFactory testStarsLevelFactory = new TestStarsLevelFactory();

    public TestStarsLevel() {
        super("Test Stars");
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        testStarsLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testStarsLevelFactory;
    }
}
