package ru.hse.team.database.levels;

import java.util.List;

public interface LevelsDao {

    List<SavedLevel> getAll();

    SavedLevel getById(long id);

    SavedLevel getBestByLevelName (String levelName);

    void insert(SavedLevel statistics);

    void insertAll(SavedLevel ... statistics);

    void update(SavedLevel statistics);

    void delete(SavedLevel statistics);

    void deleteAll(SavedLevel ... statistics);
}
