package ru.hse.team.database.statistics;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {LevelStatistics.class}, version = 4)
public abstract class StatisticsDatabaseAndroid extends RoomDatabase implements StatisticsDatabase {
    public abstract StatisticsDaoAndroid statisticsDao();
}