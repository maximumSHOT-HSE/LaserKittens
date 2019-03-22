package com.example.learning.gamelogic.components;

import com.badlogic.ashley.core.Component;

public class StateComponent implements Component {
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
}