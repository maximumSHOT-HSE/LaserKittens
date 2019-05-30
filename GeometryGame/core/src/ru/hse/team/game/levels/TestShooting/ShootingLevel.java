package ru.hse.team.game.levels.TestShooting;

import com.badlogic.ashley.core.PooledEngine;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class ShootingLevel extends AbstractLevel {

    private ShootingLevelFactory shootingLevelFactory;

    public ShootingLevel() {
        super("Test Shooting", 1, 1);
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        shootingLevelFactory = new ShootingLevelFactory(engine, assetManager, getBodyFactory());
        shootingLevelFactory.createLevel(getWidthInScreens(), getHeightInScreens(), this);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return shootingLevelFactory;
    }
}
