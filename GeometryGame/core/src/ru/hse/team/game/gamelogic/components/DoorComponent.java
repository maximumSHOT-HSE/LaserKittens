package ru.hse.team.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

import java.util.HashSet;
import java.util.Set;

public class DoorComponent implements Component, Pool.Poolable {

    private Set<Entity> keys = new HashSet<>();

    public void addKey(Entity key) {
        keys.add(key);
    }

    public void removeKey(Entity key) {
        keys.remove(key);
    }

    public int remainingKeys() {
        return keys.size();
    }

    @Override
    public void reset() {
        keys.clear();
    }
}
