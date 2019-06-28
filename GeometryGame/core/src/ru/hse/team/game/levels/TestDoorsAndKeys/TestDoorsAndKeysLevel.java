package ru.hse.team.game.levels.TestDoorsAndKeys;

import com.badlogic.ashley.core.PooledEngine;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class TestDoorsAndKeysLevel extends AbstractLevel {
    private TestDoorsAndKeysLevelFactory testDoorsAndKeysLevelFactory;

    public TestDoorsAndKeysLevel() {
        super("Door and Key", 2, 1);
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        testDoorsAndKeysLevelFactory = new TestDoorsAndKeysLevelFactory(
            engine,
            assetManager,
            getBodyFactory()
        );
        testDoorsAndKeysLevelFactory.createLevel(
            getWidthInScreens(),
            getHeightInScreens(),
            this
        );
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return testDoorsAndKeysLevelFactory;
    }
}
