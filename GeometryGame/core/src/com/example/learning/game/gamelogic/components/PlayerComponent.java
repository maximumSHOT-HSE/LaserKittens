package com.example.learning.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PlayerComponent implements Component, Poolable {

    public boolean readyToFinish() {
        return getCatchedStars() >= MAX_CATCHED_STARS;
    }

    private static final int MAX_CATCHED_STARS = 3;

    private int catchedStars;

    public int getCatchedStars() {
        return catchedStars;
    }

    public void catchStar() {
        catchedStars++;
    }

    @Override
    public void reset() {

    }
}
