package ru.hse.team.game.Multiplayer;

import com.badlogic.gdx.math.Vector2;

import org.json.JSONObject;

public class MessageCreator {
    public static final String CATCH_KEY = "catchKey";
    public static final String CATCH_STAR = "catchStar";

    public static final String SHOOT = "shoot";
    public static final String SHOOT_SOURCE = "shootSource";
    public static final String SHOOT_DIRECTION = "shootDirection";
    public static final String SHOOT_LIFETIME = "shootLifetime";

    public static final String TYPE = "type";
    public static final String KEY_ID = "keyId";
    public static final String STAR_ID = "starId";

    public static String createFinishKeyMessage(int id) {
        JSONObject data = new JSONObject();
        data.put(TYPE, CATCH_KEY);
        data.put(KEY_ID, id);
        return data.toString();
    }

    public static String createFinishStarMessage(int id) {
        JSONObject data = new JSONObject();
        data.put(TYPE, CATCH_STAR);
        data.put(STAR_ID, id);
        return data.toString();
    }

    public static String createShootMessage(
        Vector2 source,
        Vector2 direction,
        int lifeTime
    ) {
        JSONObject data = new JSONObject();
        data.put(TYPE, SHOOT);
        data.put(SHOOT_SOURCE + "x", Float.toString(source.x));
        data.put(SHOOT_SOURCE + "y", Float.toString(source.y));
        data.put(SHOOT_DIRECTION + "x", Float.toString(direction.x));
        data.put(SHOOT_DIRECTION + "y", Float.toString(direction.y));
        data.put(SHOOT_LIFETIME, lifeTime);
        return data.toString();
    }
}
