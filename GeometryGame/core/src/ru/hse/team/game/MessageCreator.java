package ru.hse.team.game;

import org.json.JSONObject;

public class MessageCreator {

    public static final String CATCH_KEY = "catch key";
    public static final String TYPE = "type";
    public static final String KEY_ID = "key id";

    public static String createFinishKeyMessage(int id) {
        JSONObject data = new JSONObject();
        data.put(TYPE, CATCH_KEY);
        data.put(KEY_ID, id);
        return data.toString();
    }
}
