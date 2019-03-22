package com.example.learning.gamelogic.components;

import com.badlogic.ashley.core.Component;
/*
 * Stores the type of entity this is
 */
public class TypeComponent implements Component {
    public enum ObjectType {
        PLAYER,
        OTHER;
    }

    public ObjectType type = ObjectType.OTHER;

}
