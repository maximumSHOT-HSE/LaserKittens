package ru.hse.team.game.gamelogic;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.hse.team.game.GameScreen;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;

/**
 * Maintains current game status
 */
public class GameStatus {

    private GameScreen gameScreen;
    private BitmapFont font;
    private SpriteBatch batch;

    private long startNano = 0;
    private long stopNano = 0;

    private boolean started = false;
    private boolean stopped = false;

    private OrthographicCamera statusCamera = new OrthographicCamera(RenderingSystem.SCREEN_WIDTH, RenderingSystem.SCREEN_HEIGHT);

    public GameStatus(GameScreen gameScreen, BitmapFont font, SpriteBatch batch) {
        this.gameScreen = gameScreen;
        this.font = font;
        this.batch = batch;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void start() {
        if (started) {
            return;
        }
        started = true;
        startNano = System.nanoTime();
    }

    public void stop() {
        if (stopped) {
            return;
        }
        stopped = true;
        stopNano = System.nanoTime();
        putEndTime();
    }

    public long timeGone() {
        long start = 0;
        long end = 0;
        if (startNano != 0) {
            start = startNano;
            end = System.nanoTime();
            if (stopNano != 0) {
                end = stopNano;
            }
        }
        return end - start;
    }

    public static String getTimeStamp(long timeNano) {
        SimpleDateFormat dateFormat = (SimpleDateFormat)SimpleDateFormat.getTimeInstance();
        dateFormat.applyPattern("mm:ss:SS");
        return dateFormat.format(new Date(TimeUnit.NANOSECONDS.toMillis(timeNano)));
    }

    private String endDate = "Date";

    private void putEndTime() {
        Calendar nowCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = (SimpleDateFormat)SimpleDateFormat.getTimeInstance();
        dateFormat.applyPattern("yyyy/MM/dd hh:mm aa");
        endDate = dateFormat.format(nowCalendar.getTime());
    }

    public String getCalendarDate() {
        return endDate;
    }

    public long getStopNanoTime() {
        return stopNano;
    }

    private int starCounter = 0;
    private int starsInLevel = 0;

    private float currentTimeToEnd = 0f;
    private float minEndTime = 1f;

    public void addStar() {
        starCounter++;
        starsInLevel++;
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

    public int getStarsInLevel() {
        return starsInLevel;
    }

    public void draw() {

        if (gameScreen.getGame().getPreferences().isShowTime()) {
            statusCamera.zoom = 10f;
            statusCamera.update();

            batch.setProjectionMatrix(statusCamera.combined);
            batch.begin();
            font.draw(batch, getTimeStamp(timeGone()), -statusCamera.zoom * RenderingSystem.SCREEN_WIDTH / 2, statusCamera.zoom * RenderingSystem.SCREEN_HEIGHT / 2);
            batch.end();
        }
    }

    public void dispose() {

    }
}
