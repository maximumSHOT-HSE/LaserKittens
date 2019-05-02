package com.example.learning.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

public interface StatisticsDao {

    List<LevelStatistics> getAll();

    LevelStatistics getById(long id);

    void insert(LevelStatistics statistics);

    void insertAll(LevelStatistics ... statistics);

    void update(LevelStatistics statistics);

    void delete(LevelStatistics statistics);

    void deleteAll(LevelStatistics ... statistics);
}

