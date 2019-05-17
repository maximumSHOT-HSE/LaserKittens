package ru.hse.team.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class KeyComponent implements Component, Poolable {

    public Entity door = null;

    @Override
    public void reset() {
        door = null;
    }
}
