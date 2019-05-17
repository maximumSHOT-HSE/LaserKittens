package ru.hse.team.game.levels.TestShooting;

import com.badlogic.ashley.core.PooledEngine;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class ShootingLevel extends AbstractLevel {

    private ShootingLevelFactory shootingLevelFactory = new ShootingLevelFactory();

    public ShootingLevel() {
        super("Test Shooting");
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        shootingLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return shootingLevelFactory;
    }
}
