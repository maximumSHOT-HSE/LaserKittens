package ru.hse.team.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Stores the type of entity.
 * This data is used to handle entities interaction
 */
public class TypeComponent implements Component, Poolable {

    public enum Type {
        PLAYER,
        BULLET,
        IMPENETRABLE_WALL,
        GUARDIAN,
        TRANSPARENT_WALL,
        DOOR,
        MIRROR,
        TUMBLER,
        STAR,
        KEY,
        OTHER
    }

    public Type type = Type.OTHER;

    @Override
    public void reset() {
        type = type.OTHER;
    }
}
