package com.example.learning.game.levels;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.MyAssetManager;

public class TestLaserAbstractLevel extends AbstractLevel {

    public TestLaserAbstractLevel() {
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
