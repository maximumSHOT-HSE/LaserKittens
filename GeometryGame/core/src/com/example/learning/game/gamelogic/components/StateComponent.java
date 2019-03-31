package com.example.learning.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class StateComponent implements Component, Poolable {

    public enum State {
        NORMAL,
        FINISHED
    }

    private State state = State.NORMAL;

    public void set(StateComponent.State newState){
        state = newState;
    }

    public State get(){
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
