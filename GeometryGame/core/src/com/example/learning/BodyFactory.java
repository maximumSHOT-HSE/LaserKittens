package com.example.learning;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class BodyFactory {

    private World world;
    private static BodyFactory bodyFactory;

    private BodyFactory(World world) {
        this.world = world;
    }

    public static BodyFactory getBodyFactory(World world) {
        if (bodyFactory == null) {
            bodyFactory = new BodyFactory(world);
        }
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
        boxBodyDef.linearVelocity.y = -1000f;
        boxBodyDef.angle = 0;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        boxBody.createFixture(newFixture(circleShape));
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

    public Body newPolygonBody(Vector2[] polygonVertices, Vector2 leftDownCorner, BodyDef.BodyType bodyType) {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = leftDownCorner.x;
        boxBodyDef.position.y = leftDownCorner.y;

        Body boxBody = world.createBody(boxBodyDef);

        PolygonShape polygon = new PolygonShape();
        polygon.set(polygonVertices);
        boxBody.createFixture(newFixture(polygon));
        polygon.dispose();

        return boxBody;
    }
}
