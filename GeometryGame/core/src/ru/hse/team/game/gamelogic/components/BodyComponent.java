package ru.hse.team.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Contains information about current entity body.
 */
public class BodyComponent implements Component, Poolable {
    /** Box2D body for entity */
    public Body body;

    @Override
    public void reset() {
        body = null;
    }
}
