package com.example.learning.game.levels;

import com.badlogic.ashley.core.PooledEngine;
import com.example.learning.KittensAssetManager;

/**
 * Class for encapsulating logic and
 * data related with level
 * (creating, drawing and storing data, e.g. score, times, etc.).
 * */
abstract public class AbstractLevel {
    // name of level
    private String name;

    public AbstractLevel(String name) {
        this.name = name;
    }

    abstract public void createLevel(PooledEngine engine, KittensAssetManager assetManager);

    public String getName() {
        return name;
    }

    abstract public AbstractLevelFactory getFactory();
}
