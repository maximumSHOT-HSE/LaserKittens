package com.example.learning.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class LevelStatistics {

    public LevelStatistics(int id, String timeStamp, int stars) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.stars = stars;
    }

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "time")
    public String timeStamp;

    @ColumnInfo(name = "stars")
    public int stars;

    @Override
    public String toString() {
        return "Level " + id + " statistics:" +
                " time " + timeStamp +
                ", stars" + stars +
                ".";
    }
}
