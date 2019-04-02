package com.example.learning.game.gamelogic;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.example.learning.game.GameScreen;

public class GameStatus {

    private GameScreen gameScreen;

    public GameStatus(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    private int starCounter = 0;

    private float currentTime = 0f;
    private float minEndTime = 1f;

    public void addStar() {
        starCounter++;
    }
    public void removeStar() {
        starCounter--;
    }

    public void addTime(float delta) {
        currentTime += delta;
    }

    public boolean readyToFinish() {
        return currentTime > minEndTime && starCounter == 0;
    }
}
