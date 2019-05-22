package ru.hse.team.game.gamelogic;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.hse.team.game.GameScreen;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;

/**
 * Maintains current game status
 */
public class GameStatus {

    private GameScreen gameScreen;
    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private long startNano = 0;

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

    public void start() {
        startNano = System.nanoTime();
    }

    private int starCounter = 0;

    private float currentTimeToEnd = 0f;
    private float minEndTime = 1f;

    public void addStar() {
        starCounter++;
    }

    public void removeStar() {
        starCounter--;
    }

    public void update(float delta) {
        currentTimeToEnd += delta;
    }

    public boolean readyToFinish() {
        return currentTimeToEnd > minEndTime && starCounter == 0;
    }

    public int getStarCounter() {
        return starCounter;
    }

    public void draw() {
        batch.begin();
//        font.draw(batch, Long.toString(System.nanoTime() - startNano), 0, 0);
        batch.end();
    }
}
