package ru.hse.team.database.levels;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {SavedLevel.class}, version = 4, exportSchema = false)
@TypeConverters({SavedLevelsConverter.class})
public abstract class LevelsDatabaseAndroid extends RoomDatabase implements LevelsDatabase {
    public abstract LevelsDaoAndroid levelsDao();
}