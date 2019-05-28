package ru.hse.team.database.levels;

import java.util.List;

public interface LevelsDao {

    List<SavedLevel> getAll();

    SavedLevel getById(long id);

    void insert(SavedLevel level);

    void insertAll(SavedLevel ... level);

    void update(SavedLevel level);

    void delete(SavedLevel level);

    void deleteAll(SavedLevel ... levels);
}
