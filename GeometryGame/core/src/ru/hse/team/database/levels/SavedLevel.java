package ru.hse.team.database.levels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

/**
 * Representation of a simple level.
 * Created for serialising/deserialising level and saving it in database
 * Contains List of {@code SimpleEntity}, which are level objects
 *  and some level characteristics
 */
@android.arch.persistence.room.Entity
public class SavedLevel {
    public SavedLevel(int id, List<SimpleEntity> entities,
                      int widthInScreens,
                      int heightInScreens,
                      String levelName) {
        this.id = id;
        this.entities = entities;
        this.widthInScreens = widthInScreens;
        this.heightInScreens = heightInScreens;
        this.levelName = levelName;
    }

    @PrimaryKey
    public int id;

    @ColumnInfo
    public List<SimpleEntity> entities;

    @ColumnInfo
    public int widthInScreens;

    @ColumnInfo
    public int heightInScreens;

    @ColumnInfo
    public String levelName;
}
