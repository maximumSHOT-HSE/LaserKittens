package ru.hse.team.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Contains information about current entity state.
 * StateControlSystem uses it
 */
public class StateComponent implements Component, Poolable {

    public enum State {
        NORMAL,
        JUST_CREATED,
        FINISHED
    }

    private State state = State.NORMAL;

    public void set(StateComponent.State newState) {
        state = newState;
    }

    public State get() {
        return state;
    }

    public void finish() {
        state = State.FINISHED;
    }

    @Override
    public void reset() {
        state = State.NORMAL;
    }
}