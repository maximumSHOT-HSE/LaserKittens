package com.example.learning.game.gamelogic;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.learning.game.GameScreen;
import com.example.learning.game.gamelogic.systems.RenderingSystem;

/**
 * Maintains current game status
 */
public class GameStatus {

    private GameScreen gameScreen;
    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    public GameStatus(GameScreen gameScreen, BitmapFont font, SpriteBatch batch) {
        this.gameScreen = gameScreen;
        this.font = font;
        this.batch = batch;
        camera = new OrthographicCamera(RenderingSystem.SCREEN_WIDTH, RenderingSystem.SCREEN_HEIGHT);
        camera.position.set(RenderingSystem.SCREEN_WIDTH / 2f, RenderingSystem.SCREEN_HEIGHT / 2f, 0);
        draw();
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

    public int getStarCounter() {
        return starCounter;
    }

    public void draw() {
    }
}
