package ru.hse.team.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.HashSet;
import java.util.Set;

public class DoorComponent implements Component, Pool.Poolable {

    private static final int MAX_HITS_TO_HINT_NUMBER = 5;
    private static final long MAX_HINTS_VISIBLE_TIME = 5_000;

    private Set<Entity> keys = new HashSet<>();
    private int doorHits = MAX_HITS_TO_HINT_NUMBER;
    private long lastHintsShowTime = (long) -1e9;

    public void addKey(Entity key) {
        keys.add(key);
    }

    public void removeKey(Entity key) {
        keys.remove(key);
    }

    public int remainingKeys() {
        return keys.size();
    }

    public Set<Entity> getKeys() {
        return keys;
    }

    public int getDoorHits() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHintsShowTime > MAX_HINTS_VISIBLE_TIME) {
            doorHits = MAX_HITS_TO_HINT_NUMBER;
            lastHintsShowTime = currentTime;
        }
        return doorHits;
    }

    public void hitDoor() {
        if (doorHits > 0) {
            doorHits--;
        }
    }

    @Override
    public void reset() {
        keys.clear();
        doorHits = MAX_HITS_TO_HINT_NUMBER;
    }
}
