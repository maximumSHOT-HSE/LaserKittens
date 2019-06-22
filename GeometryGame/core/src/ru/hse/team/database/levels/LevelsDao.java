package ru.hse.team.database.levels;

import java.util.List;

/**
 * Interface for saving {@code SavedLevel.class} in Android Room database.
 */
public interface LevelsDao {

    List<SavedLevel> getAll();

    void insert(SavedLevel level);

    void update(SavedLevel level);

    void delete(SavedLevel level);
}
