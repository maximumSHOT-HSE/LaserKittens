package ru.hse.team.game.levels.TestBigLevel;

import com.badlogic.ashley.core.PooledEngine;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class TestBigLevel extends AbstractLevel {

    private TestBigLevelFactory testBigLevelFactory;

    public TestBigLevel() {
        super("Test Big Level", 3, 3);
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        testBigLevelFactory = new TestBigLevelFactory(engine, assetManager, getBodyFactory());
        testBigLevelFactory.createLevel(getWidthInScreens(), getHeightInScreens(), this);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testBigLevelFactory;
    }
}

