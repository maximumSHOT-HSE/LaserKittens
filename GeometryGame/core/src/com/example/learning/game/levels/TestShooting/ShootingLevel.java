package com.example.learning.game.levels.TestShooting;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.MyAssetManager;
import com.example.learning.game.levels.AbstractLevel;
import com.example.learning.game.levels.AbstractLevelFactory;

public class ShootingLevel extends AbstractLevel {

    private ShootingLevelFactory shootingLevelFactory = new ShootingLevelFactory();

    public ShootingLevel() {
        super("Test Shooting");
    }

    @Override
    public void createLevel(PooledEngine engine, MyAssetManager assetManager) {
        shootingLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return shootingLevelFactory;
    }
}
