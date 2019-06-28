package ru.hse.team.database.statistics;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Contains information about successfully finished level.
 */
@Entity
public class LevelStatistics {
    public LevelStatistics(String levelName, long timeNano, String date) {
        this.timeNano = timeNano;
        this.levelName = levelName;
        this.date = date;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "levelName")
    final public String levelName;

    @ColumnInfo(name = "time")
    final public long timeNano;

    @ColumnInfo(name = "date")
    public String date;

    @Override
    public String toString() {
        return "Level " + id
                + " statistics:"
                + " time " + timeNano
                + ", level name: " + levelName
                + ".";
    }
}
