package ru.hse.team.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Contains runnable action that can be called on some event happened with entity.
 */
public class TumblerComponent implements Component, Pool.Poolable {
    private Runnable action;

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    @Override
    public void reset() {
        action = null;
    }
}
