package com.example.learning.game.levels.TestLaserLevel;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.MyAssetManager;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestLaserLevel extends AbstractLevel {

    private TestLaserLevelFactory laserLevelFactory = new TestLaserLevelFactory();

    public TestLaserLevel() {
        super("Test Laser");
    }

    @Override
    public void createLevel(PooledEngine engine, MyAssetManager assetManager) {
        laserLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return laserLevelFactory;
    }
}
