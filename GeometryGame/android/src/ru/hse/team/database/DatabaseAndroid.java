package ru.hse.team.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import ru.hse.team.database.levels.LevelsDaoAndroid;
import ru.hse.team.database.levels.SavedLevel;
import ru.hse.team.database.levels.SavedLevelsConverter;
import ru.hse.team.database.statistics.LevelStatistics;
import ru.hse.team.database.statistics.StatisticsDaoAndroid;

/**
 *  Database used to save {@code SavedLevel} and {@code LevelStatistics} objects.
 */
@Database(entities = {SavedLevel.class, LevelStatistics.class}, version = 7, exportSchema = false)
@TypeConverters({SavedLevelsConverter.class})
public abstract class DatabaseAndroid extends RoomDatabase implements GameDatabase {
    public abstract LevelsDaoAndroid levelsDao();
    public abstract StatisticsDaoAndroid statisticsDao();
}