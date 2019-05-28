package ru.hse.team.database.statistics;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class LevelStatistics {

    public LevelStatistics(String levelName, long timeNano, int stars, String date) {
        this.timeNano = timeNano;
        this.stars = stars;
        this.levelName = levelName;
        this.date = date;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "levelName")
    public String levelName;

    @ColumnInfo(name = "time")
    public long timeNano;

    @ColumnInfo(name = "stars")
    public int stars;

    @ColumnInfo(name = "date")
    public String date;


    @Override
    public String toString() {
        return "Level " + id + " statistics:" +
                " time " + timeNano +
                ", stars" + stars +
                ", level name: " + levelName +
                ".";
    }
}
