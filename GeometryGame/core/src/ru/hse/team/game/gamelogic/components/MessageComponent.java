package ru.hse.team.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;


public class MessageComponent implements Component, Poolable {
    private static final long NEGATIVE_TIME_INFINITY = (long) -1e9;

    public long lastTimeShown = NEGATIVE_TIME_INFINITY;
    public boolean likeToShow = false;
    public String message = "";

    @Override
    public void reset() {
        lastTimeShown = NEGATIVE_TIME_INFINITY;
        likeToShow = false;
        message = "";
    }
}
