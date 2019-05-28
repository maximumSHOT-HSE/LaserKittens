package ru.hse.team.database.levels;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SavedLevelsConverter {
    @TypeConverter
    public static List<SimpleEntity> fromString(String value) {
        Type listType = new TypeToken<List<SimpleEntity>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<SimpleEntity> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
