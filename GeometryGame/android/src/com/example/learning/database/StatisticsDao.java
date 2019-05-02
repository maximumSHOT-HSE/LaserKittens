package com.example.learning.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface StatisticsDao {

    @Query("SELECT * FROM LevelStatistics")
    List<LevelStatistics> getAll();

    @Query("SELECT * FROM LevelStatistics WHERE id = :id")
    LevelStatistics getById(long id);

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
