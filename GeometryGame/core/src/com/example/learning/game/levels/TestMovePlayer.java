package com.example.learning.game.levels;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.MyAssetManager;

public class TestMovePlayer extends AbstractLevel {

    public TestMovePlayer() {
        super("Test Move");
    }

    @Override
    public void createLevel() {

    }

    @Override
    public AbstractLevelFactory getFactory(PooledEngine engine, MyAssetManager assetManager) {
        return null;
    }
}
