package ru.hse.team.game.gamelogic.algorithms;

import com.badlogic.gdx.math.Vector2;

/**
 * Class with static methods related with
 * geometry calculations
 */
public class Geometry {
    private static final float EPS = (float) 1e-3;

    public static boolean floatEquals(float x, float y) {
        return Math.abs(x - y) < EPS;
    }

    public static boolean pointEquals(Vector2 a, Vector2 b) {
        return floatEquals(a.x, b.x) && floatEquals(a.y, b.y);
    }

    public static boolean inRectangle(Vector2 point, Vector2 center, float width, float height) {
        return center.x - width / 2 <= point.x
                && point.x <= center.x + width / 2
                && center.y - height / 2 <= point.y
                && point.y <= center.y + height / 2;
    }

    public static float vectorMultiplication(Vector2 v1, Vector2 v2) {
        return v1.x * v2.y - v2.x * v1.y;
    }

    public static float scalarMultiplication(Vector2 v1, Vector2 v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public static boolean areIntersected(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2) {
        if (pointEquals(a1, b1)
                || pointEquals(a1, b2)
                || pointEquals(a2, b1)
                || pointEquals(a2, b2)) {
            return true;
        }
        float val1, val2;

        val1 = vectorMultiplication(b1.cpy().sub(a1), a2.cpy().sub(a1));
        val2 = vectorMultiplication(b2.cpy().sub(a1), a2.cpy().sub(a1));

        if (val1 > 0 && val2 > 0 || val1 < 0 && val2 < 0) {
            return false;
        }

        val1 = vectorMultiplication(a1.cpy().sub(b1), b2.cpy().sub(b1));
        val2 = vectorMultiplication(a2.cpy().sub(b1), b2.cpy().sub(b1));

        if (val1 > 0 && val2 > 0 || val1 < 0 && val2 < 0) {
            return false;
        }

        return true;
    }

    // segment and rectangle
    public static boolean areIntersected(
        Vector2 a1,
        Vector2 b1,
        Vector2 center,
        float width,
        float height
    ) {
        if (inRectangle(a1, center, width, height)) {
            return true;
        }
        if (inRectangle(b1, center, width, height)) {
            return true;
        }
        Vector2 A = new Vector2(center.x - width / 2, center.y - height / 2);
        Vector2 B = new Vector2(center.x - width / 2, center.y + height / 2);
        Vector2 C = new Vector2(center.x + width / 2, center.y + height / 2);
        Vector2 D = new Vector2(center.x + width / 2, center.y - height / 2);
        if (areIntersected(a1, b1, A, B)) {
            return true;
        }
        if (areIntersected(a1, b1, B, C)) {
            return true;
        }
        if (areIntersected(a1, b1, C, D)) {
            return true;
        }
        if (areIntersected(a1, b1, D, A)) {
            return true;
        }
        return false;
    }

    /**
     * Finds distance from point to segment in one-dimensional space.
     * */
    public static float distanceToSegment(float x, float leftX, float rightX) {
        float distance = 0;
        if (x < leftX) {
            distance = Math.max(distance, leftX - x);
        }
        if (x > rightX) {
            distance = Math.max(distance, x - rightX);
        }
        return distance;
    }
}
