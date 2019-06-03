package ru.hse.team.database;

import ru.hse.team.database.levels.LevelsDao;
import ru.hse.team.database.statistics.StatisticsDao;

public interface GameDatabase {
    LevelsDao levelsDao();
    StatisticsDao statisticsDao();
}
