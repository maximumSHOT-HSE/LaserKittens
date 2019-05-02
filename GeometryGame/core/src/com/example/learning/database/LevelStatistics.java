package com.example.learning.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class LevelStatistics {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "time")
    public String timeStamp;

    @ColumnInfo(name = "stars")
    public int stars;
}
