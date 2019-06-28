package ru.hse.team.database;

import ru.hse.team.database.levels.LevelsDao;
import ru.hse.team.database.statistics.StatisticsDao;

/**
 *  Database for {@code SavedLevel} and {@code LevelStatistics} objects.
 */
public interface GameDatabase {
    LevelsDao levelsDao();
    StatisticsDao statisticsDao();
}
