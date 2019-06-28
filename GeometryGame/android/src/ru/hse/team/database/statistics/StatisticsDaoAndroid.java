package ru.hse.team.database.statistics;

import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import java.util.List;


/**
 * Class which allows to access {@code LevelStatistics} objects in database.
 */
@Dao
public interface StatisticsDaoAndroid extends StatisticsDao {
    @RawQuery
    LevelStatistics getStatisticsByQuery(SupportSQLiteQuery query);

    @Query("SELECT * FROM LevelStatistics")
    List<LevelStatistics> getAll();

    @Query("SELECT * FROM LevelStatistics WHERE id = :id")
    LevelStatistics getById(long id);

    @Query("SELECT * FROM LevelStatistics WHERE levelName = :levelName AND time = (SELECT MIN(time) FROM LevelStatistics where levelName = :levelName)")
    LevelStatistics getBestByLevelName (String levelName);

    @Insert
    void insert(LevelStatistics statistics);

    @Insert
    void insertAll(LevelStatistics ... statistics);

    @Update
    void update(LevelStatistics statistics);

    @Delete
    void delete(LevelStatistics statistics);

    @Delete
    void deleteAll(LevelStatistics ... statistics);
}
