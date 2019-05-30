package ru.hse.team.game.levels.TestLongCorridor;

import com.badlogic.ashley.core.PooledEngine;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class TestLongCorridorLevel extends AbstractLevel {
    private TestLongCorridorFactory testLongCorridorFactory;

    public TestLongCorridorLevel() {
        super("Long Corridor", 1, 5);
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        testLongCorridorFactory = new TestLongCorridorFactory(engine, assetManager, getBodyFactory());
        testLongCorridorFactory.createLevel(getLevelWidthInScreens(), getLevelHeightInScreens(), this);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testLongCorridorFactory;
    }
}
