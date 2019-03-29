package com.example.learning.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool.Poolable;

public class StateComponent implements Component, Poolable {

    public enum State {
        NORMAL,
        SOME,
        FINISHED
    }

    private State state = State.NORMAL;

    public void set(StateComponent.State newState){
        state = newState;
    }

    public State get(){
        return state;
    }

    @Override
    public void reset() {
        state = State.NORMAL;
    }

    public void finish() {
        state = State.FINISHED;
    }
}