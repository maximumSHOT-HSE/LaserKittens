package com.example.learning.game.gamelogic;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class GameStatus {

    private int starCounter = 0;

    public void addStar() {
        starCounter++;
    }

    public void removeStar() {
        starCounter--;
    }

    public boolean readyToFinish() {
        return starCounter == 0;
    }
}
