package ru.hse.team.database.levels;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Class which allows to access {@code SavedLevel} objects in database.
 */
@Dao
public interface LevelsDaoAndroid extends LevelsDao {

    @Query("SELECT * FROM SavedLevel")
    List<SavedLevel> getAll();

    @Insert
    void insert(SavedLevel level);

    @Update
    void update(SavedLevel level);

    @Delete
    void delete(SavedLevel level);

}
