package ru.hse.team.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {LevelStatistics.class}, version = 4)
public abstract class AppDatabaseAndroid extends RoomDatabase implements AppDatabase {
    public abstract StatisticsDaoAndroid statisticsDao();
}