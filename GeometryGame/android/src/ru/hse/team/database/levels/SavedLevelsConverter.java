package ru.hse.team.database.levels;

import android.arch.persistence.room.TypeConverter;

import com.badlogic.ashley.core.Entity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SavedLevelsConverter {
    @TypeConverter
    public static List<SavedSimpleEntity> fromString(String value) {
        Type listType = new TypeToken<List<SavedSimpleEntity>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<SavedSimpleEntity> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
