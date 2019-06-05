package ru.hse.team.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;


public class MessageComponent implements Component, Poolable {

    //seconds
    public static final long SHOWING_INTERVAL = 2;


    public long lastTimeShown = (long)-1e9;
    public boolean likeToShow = false;
    public String message = "";

    @Override
    public void reset() {
        lastTimeShown = (long)-1e9;
        likeToShow = false;
        message = "";
    }
}
