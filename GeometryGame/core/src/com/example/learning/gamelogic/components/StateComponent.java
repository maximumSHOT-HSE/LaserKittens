package com.example.learning.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class StateComponent implements Component, Poolable {

    public enum State {
        NORMAL,
        SOME;
    }

    private State state = State.NORMAL;
    public float time = 0.0f;
    public boolean isLooping = false;

    public void set(StateComponent.State newState){
        state = newState;
        time = 0.0f;
    }

    public State get(){
        return state;
    }

    @Override
    public void reset() {
        state = State.NORMAL;
        time = 0.0f;
        isLooping = false;
    }
}