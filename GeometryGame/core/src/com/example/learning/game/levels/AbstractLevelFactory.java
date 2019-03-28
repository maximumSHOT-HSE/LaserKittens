package com.example.learning.game.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.MyAssetManager;

abstract public class AbstractLevelFactory {

    abstract public World getWorld();

    abstract public Entity getPlayer();

    abstract public void createLevel(PooledEngine engine, MyAssetManager assetManager);
}
