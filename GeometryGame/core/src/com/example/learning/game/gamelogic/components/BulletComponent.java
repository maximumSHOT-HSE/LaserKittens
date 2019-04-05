package com.example.learning.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

import java.util.ArrayList;

public class BulletComponent implements Component, Poolable {

    /** Time of bullet creation in milliseconds */
    public long creationTime;

    /** Time life of bullet in milliseconds */
    public float lifeTime;

    /** list of broken line points on it's way */
    public java.util.List<Vector2> path = new ArrayList<>();

    @Override
    public void reset() {
        creationTime = 0;
        lifeTime = 0;
        path = new ArrayList<>();
    }
}
