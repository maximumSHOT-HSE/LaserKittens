package com.example.learning.game.gamelogic;

import com.example.learning.game.GameScreen;

/**
 * Maintains current game status
 */
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
