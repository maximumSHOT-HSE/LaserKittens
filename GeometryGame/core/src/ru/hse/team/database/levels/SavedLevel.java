package ru.hse.team.database.levels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;

import com.badlogic.ashley.core.Entity;

import java.util.List;

@android.arch.persistence.room.Entity
public class SavedLevel {

    public SavedLevel(List<SavedSimpleEntity> entities, int widthInScreens, int heightInScreens) {
        this.entities = entities;
        this.widthInScreens = widthInScreens;
        this.heightInScreens = heightInScreens;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;


    @ColumnInfo
    public List<SavedSimpleEntity> entities;

    @ColumnInfo
    public int widthInScreens;

    @ColumnInfo
    public int heightInScreens;


}
