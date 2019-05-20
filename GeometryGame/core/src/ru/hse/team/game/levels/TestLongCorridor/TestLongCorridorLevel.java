package ru.hse.team.game.levels.TestLongCorridor;

import com.badlogic.ashley.core.PooledEngine;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class TestLongCorridorLevel extends AbstractLevel {
    private TestLongCorridorFactory testLongCorridorFactory;

    public TestLongCorridorLevel() {
        super("Long Corridor");
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        testLongCorridorFactory = new TestLongCorridorFactory();
        testLongCorridorFactory.setLevelSize(1, 5);
        testLongCorridorFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testLongCorridorFactory;
    }
}
