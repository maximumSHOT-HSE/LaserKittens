package com.example.learning.game.levels.TestBigLevel;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestBigLevel extends AbstractLevel {

    private TestBigLevelFactory testBigLevelFactory = new TestBigLevelFactory();

    public TestBigLevel() {
        super("Test Big Level");
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        testBigLevelFactory.setLevelSize(3, 3);
        testBigLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testBigLevelFactory;
    }
}

