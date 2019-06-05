package ru.hse.team.game.gamelogic;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;

/**
 * Maintains current game status.
 * Which is number of collected stars and time from level start
 */
public class GameStatus {

    private long startNano = 0;
    private long stopNano = 0;
    private long penaltyNano = 0;

    private boolean started = false;
    private boolean stopped = false;

    private OrthographicCamera statusCamera = new OrthographicCamera(
            RenderingSystem.SCREEN_WIDTH, RenderingSystem.SCREEN_HEIGHT);

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

    /**
     * Time in nanoseconds from level start with
     *  added penalty time.
     */
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
        return end - start + penaltyNano;
    }

    /**
     * Translate given time in nanos to string in pattern "mm:ss:SS".
     * m - for minutes, s - for seconds and S - for milliseconds.
     */
    public static String getTimeStamp(long timeNano) {
        SimpleDateFormat dateFormat = (SimpleDateFormat)SimpleDateFormat.getTimeInstance();
        dateFormat.applyPattern("mm:ss:SS");
        return dateFormat.format(new Date(TimeUnit.NANOSECONDS.toMillis(timeNano)));
    }

    private String endDate = "Date";

    private void putEndTime() {
        Calendar nowCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = (SimpleDateFormat)SimpleDateFormat.getTimeInstance();
        dateFormat.applyPattern("yyyy/MM/dd HH:mm");
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
    private float minEndTime = TimeUnit.MILLISECONDS.toSeconds(1);

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

    /**
     * Returns whether level is finished.
     * Level is finished if all stars are collected
     */
    public boolean readyToFinish() {
        return currentTimeToEnd > minEndTime && starCounter == 0;
    }

    public int getStarsInLevel() {
        return starsInLevel;
    }

    public void addPenaltyNanoTime(long timeNano) {
        penaltyNano += timeNano;
    }

    /**
     *  Draws in top-left corner time gone from level start .
     */
    public void draw(SpriteBatch batch, BitmapFont font) {
        statusCamera.zoom = 10f;
        statusCamera.update();

        batch.setProjectionMatrix(statusCamera.combined);
        batch.begin();
        font.draw(batch, getTimeStamp(timeGone()),
                -statusCamera.zoom * RenderingSystem.SCREEN_WIDTH / 2,
                statusCamera.zoom * RenderingSystem.SCREEN_HEIGHT / 2);

        batch.end();
    }

    public void dispose() {

    }
}
