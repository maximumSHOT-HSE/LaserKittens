package com.example.learning.game.levels.TestLaserLevel;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.MyAssetManager;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestLaserLevel extends AbstractLevel {

    public TestLaserLevel() {
        super("Test Laser");
    }

    @Override
    public void createLevel(PooledEngine engine, MyAssetManager assetManager) {

    }

    @Override
    public AbstractLevelFactory getFactory() {
        return null;
    }
}
