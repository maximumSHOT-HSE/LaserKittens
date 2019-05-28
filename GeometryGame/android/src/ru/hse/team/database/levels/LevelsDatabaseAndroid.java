package ru.hse.team.database.levels;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {SavedLevel.class}, version = 1)
public abstract class LevelsDatabaseAndroid extends RoomDatabase implements LevelsDatabase {
    public abstract LevelsDaoAndroid statisticsDao();
}