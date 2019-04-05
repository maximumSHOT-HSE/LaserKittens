package com.example.learning.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

import java.util.ArrayList;

/**
 * Contains information about current entity
 * bullet component.
 * Laser is implemented via BulletComponent
 */
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
