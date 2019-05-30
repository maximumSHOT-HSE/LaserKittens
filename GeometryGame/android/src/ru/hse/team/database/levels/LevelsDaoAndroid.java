package ru.hse.team.database.levels;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface LevelsDaoAndroid extends LevelsDao {

    @Query("SELECT * FROM SavedLevel")
    List<SavedLevel> getAll();

    @Query("SELECT * FROM SavedLevel WHERE id = :id")
    SavedLevel getById(long id);

    @Insert
    void insert(SavedLevel level);

    @Insert
    void insertAll(SavedLevel ... levels);

    @Update
    void update(SavedLevel level);

    @Delete
    void delete(SavedLevel level);

    @Delete
    void deleteAll(SavedLevel ... levels);

}
