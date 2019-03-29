package com.example.learning.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BulletComponent implements Component, Poolable {

    /** Time of bullet creation in milliseconds */
    public long creationTime;

    /** Time life of bullet in milliseconds */
    public float lifeTime;

    @Override
    public void reset() {
        creationTime = 0;
        lifeTime = 0;
    }
}
