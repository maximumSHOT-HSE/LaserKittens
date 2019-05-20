package ru.hse.team.game.levels.TestBigLevel;

import com.badlogic.ashley.core.PooledEngine;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class TestBigLevel extends AbstractLevel {

    private TestBigLevelFactory testBigLevelFactory;

    public TestBigLevel() {
        super("Test Big Level");
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        testBigLevelFactory = new TestBigLevelFactory();
        testBigLevelFactory.setLevelSize(3, 3);
        testBigLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testBigLevelFactory;
    }
}

