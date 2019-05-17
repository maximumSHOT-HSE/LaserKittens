package ru.hse.team.database;

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

