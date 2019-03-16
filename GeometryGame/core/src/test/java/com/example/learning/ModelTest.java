package com.example.learning;

import com.badlogic.gdx.math.Vector2;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModelTest {

    @Test
    public void testFromListToPointsEmptyList() {
        assertArrayEquals(new Vector2[]{}, Model.fromListToPoints(new float[]{}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromListToPointsEmptyException() {
        Model.fromListToPoints(new float[]{1,2,3,4,5});
    }

    @Test
    public void testFromListToPointsFilldeValid() {
        float[] coordinates = new float[]{
            0,1,2,-3,4,-5,6,7,8,9
        };
        Vector2[] expectedPoints = new Vector2[] {
            new Vector2(0, 1),
            new Vector2(2, -3),
            new Vector2(4, -5),
            new Vector2(6, 7),
            new Vector2(8,9)
        };
        assertArrayEquals(expectedPoints, Model.fromListToPoints(coordinates));
    }
}