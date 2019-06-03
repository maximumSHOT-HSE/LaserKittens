package ru.hse.team.database.statistics;

import java.util.List;

public interface StatisticsDao {

    List<LevelStatistics> getAll();

    LevelStatistics getById(long id);

    LevelStatistics getBestByLevelName (String levelName);

    void insert(LevelStatistics statistics);

    void insertAll(LevelStatistics ... statistics);

    void update(LevelStatistics statistics);

    void delete(LevelStatistics statistics);

    void deleteAll(LevelStatistics ... statistics);

}
