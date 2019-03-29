package com.example.learning.game.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.MyAssetManager;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

abstract public class AbstractLevelFactory {

    protected PooledEngine engine;
    protected MyAssetManager manager;

    abstract public World getWorld();

    abstract public Entity getPlayer();

    public Entity createBullet(Vector2 source, Vector2 direction) {
        throw new NotImplementedException();
    }

    abstract public void createLevel(PooledEngine engine, MyAssetManager assetManager);
}
