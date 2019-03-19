package com.example.learning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

/**
 * Class contains and implements logic part of game
 */
public class Model {

    World world = new World(new Vector2(0, 0), true);
    private BodyFactory bodyFactory = BodyFactory.getBodyFactory(world);
    private OrthographicCamera camera = new OrthographicCamera();
    //private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
    private Body body;
    private GestureDetector controller;

    public Model(GestureDetector controller) {
        world.setContactListener(new MyContactListener(this));
        this.controller = controller;

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        Vector2 laserSourceCenter = new Vector2(
            0.5f * width, 0.2f * height
        );
        float laserSourceRadius = 150f;
        float windowWidth = 0.5f * width;
        float wallWidth = (width - windowWidth) * 0.5f;

//        Vector2[] fence = fromListToPoints(
//            new float[]{
//                1, 1, width - 1, 1, width - 1, height - 1, 1, height - 1
//            }
//        );

        body = bodyFactory.newCircleBody(laserSourceCenter, laserSourceRadius, BodyDef.BodyType.DynamicBody, false);
        body.setLinearVelocity(new Vector2(100, 100));
//        // left wall
//        bodyFactory.newRectangleBody(
//            new Vector2(1, 0.7f * height),
//                wallWidth, 0.02f * height,
//                BodyDef.BodyType.StaticBody,
//                false
//        );
//
//        // right wall
//        bodyFactory.newRectangleBody(
//            new Vector2(width - wallWidth, 0.7f * height),
//            wallWidth, 0.02f * height,
//                BodyDef.BodyType.StaticBody,
//                false
//        );
//
//        // plug
//        bodyFactory.newRectangleBody(
//            new Vector2(10, 10),
//            width - 3.5f * wallWidth, 0.02f * height,
//                BodyDef.BodyType.StaticBody,
//                false
//        );
    }

    public void step(float delta) {
        //world.step(delta, 3, 3);

    }


    /**
     * Converting from {x1, y1, ..., xn, yn} to {(x1, y1), ..., (xn, yn)}
     * */
    public static Vector2[] fromListToPoints(float[] coordinates) {
        if (coordinates.length % 2 == 1) {
            throw new IllegalArgumentException("Number of coordinates should be even");
        }
        Vector2[] points = new Vector2[coordinates.length / 2];
        for (int i = 0; i < coordinates.length; i += 2) {
            float xi = coordinates[i];
            float yi = coordinates[i + 1];
            points[i / 2] = new Vector2(xi, yi);
        }
        return points;
    }
}
