package ru.hse.team.game.levels.TestDoorsAndKeys;

import com.badlogic.ashley.core.PooledEngine;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

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
