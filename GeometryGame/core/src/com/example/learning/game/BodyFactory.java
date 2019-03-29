package com.example.learning.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Map;

public class BodyFactory {

    private World world;
    private static BodyFactory bodyFactory;

    private BodyFactory(World world) {
        this.world = world;
    }

    public static BodyFactory getBodyFactory(World world) {
        bodyFactory = new BodyFactory(world);
        return bodyFactory;
    }

    public static FixtureDef newFixture(Shape shape) {
        FixtureDef fixtureDef = new FixtureDef();

        // like a stone

        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.9f;
        fixtureDef.restitution = 0.01f;

        return fixtureDef;
    }

    public Body newCircleBody(Vector2 center, float radius, BodyDef.BodyType bodyType, boolean fixedRotation) {

        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = center.x;
        boxBodyDef.position.y = center.y;
        boxBodyDef.fixedRotation = fixedRotation;
        boxBodyDef.linearVelocity.y = 0f;
        boxBodyDef.angle = 0;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        FixtureDef fixtureDef = newFixture(circleShape);
        boxBody.createFixture(fixtureDef);
        circleShape.dispose();

        return boxBody;
    }

    public Body newRectangleBody(Vector2 leftDownCorner, float width, float height, BodyDef.BodyType bodyType, boolean fixedRotation){
        leftDownCorner.x += width / 2f;
        leftDownCorner.y += height / 2f;
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = leftDownCorner.x;
        boxBodyDef.position.y = leftDownCorner.y;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(width, height);
        boxBody.createFixture(newFixture(poly));
        poly.dispose();

        return boxBody;
    }

    public Body newPolygonBody(Vector2[] polygonVertices, Vector2 leftDownCorner, BodyDef.BodyType bodyType, boolean fixedRotation) {

        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = leftDownCorner.x;
        boxBodyDef.position.y = leftDownCorner.y;
        boxBodyDef.fixedRotation = fixedRotation;
        Body boxBody = world.createBody(boxBodyDef);

        PolygonShape polygon = new PolygonShape();
        polygon.set(polygonVertices);
        boxBody.createFixture(newFixture(polygon));
        polygon.dispose();


        return boxBody;
    }

    private Vector2 coordByAngle(Vector2 center, float angle, float length) {
        return new Vector2(center.x + (float)Math.cos(Math.toRadians(angle)) * length, center.y + (float)Math.sin(Math.toRadians(angle)) * length);
    }

    public Body newStar(Vector2 center, float radius, BodyDef.BodyType bodyType, boolean fixedRotation) {
        Vector2 leftDownCorner = new Vector2(center.x + radius * (float)Math.cos(Math.toRadians(234)), center.y + radius * (float)Math.sin(Math.toRadians(234f)));

        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = leftDownCorner.x;
        boxBodyDef.position.y = leftDownCorner.y;
        boxBodyDef.fixedRotation = fixedRotation;
        Body boxBody = world.createBody(boxBodyDef);

        for (int i = 0; i < 5; i++) {
            float angle = 234f - i * 72f;
            float angleR = angle - 36f;
            float angleL = angle + 36f;

            Vector2[] coordinates = {coordByAngle(center, angle, radius), coordByAngle(center, angleR, radius / 2),
                    center, coordByAngle(center, angleL, radius / 2)};

            PolygonShape polygon = new PolygonShape();
            polygon.set(coordinates);
            boxBody.createFixture(newFixture(polygon));
            polygon.dispose();
        }

        return boxBody;
    }
}
