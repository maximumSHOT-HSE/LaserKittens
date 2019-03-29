package com.example.learning.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
/*
 * Stores the type of entity this is
 */
public class TypeComponent implements Component, Poolable {

    public enum ObjectType {
        PLAYER,
        OTHER
    }

    public ObjectType type = ObjectType.OTHER;


    @Override
    public void reset() {
        type = type.OTHER;
    }
}
