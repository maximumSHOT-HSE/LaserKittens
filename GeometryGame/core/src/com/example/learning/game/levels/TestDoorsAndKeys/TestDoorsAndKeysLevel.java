package com.example.learning.game.levels.TestDoorsAndKeys;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;
import com.example.learning.game.levels.TestLongCorridor.TestLongCorridorFactory;

public class TestDoorsAndKeysLevel extends AbstractLevel {
    private TestDoorsAndKeysLevelFactory testDoorsAndKeysLevelFactory = new TestDoorsAndKeysLevelFactory();

    public TestDoorsAndKeysLevel() {
        super("Door and Key");
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        testDoorsAndKeysLevelFactory.setLevelSize(2, 1);
        testDoorsAndKeysLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testDoorsAndKeysLevelFactory;
    }
}
