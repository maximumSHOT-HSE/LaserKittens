package ru.hse.team.database.levels;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import java.util.List;
@Dao
public interface LevelsDaoAndroid extends LevelsDao {

    @RawQuery
    SavedLevel getStatisticsByQuery(SupportSQLiteQuery query);

    @Query("SELECT * FROM LevelStatistics")
    List<SavedLevel> getAll();

    @Query("SELECT * FROM LevelStatistics WHERE id = :id")
    SavedLevel getById(long id);

    @Query("SELECT * FROM LevelStatistics WHERE levelName = :levelName AND time = (SELECT MIN(time) FROM LevelStatistics where levelName = :levelName)")
    SavedLevel getBestByLevelName (String levelName);

    @Insert
    void insert(SavedLevel statistics);

    @Insert
    void insertAll(SavedLevel ... statistics);

    @Update
    void update(SavedLevel statistics);

    @Delete
    void delete(SavedLevel statistics);

    @Delete
    void deleteAll(SavedLevel ... statistics);

}
