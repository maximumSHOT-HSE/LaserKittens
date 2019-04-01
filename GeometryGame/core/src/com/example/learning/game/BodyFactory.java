package com.example.learning.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.example.learning.game.gamelogic.systems.RenderingSystem;

/**
 * Utility class for creating bodies.
 */
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


    private enum Category {

        PLAYER((short)1),
        BULLET((short)2),
        OTHER((short)4);

        private Category(short mask) { this.mask = mask; }

        private static short all() {
            short sum_mask = 0;
            for (Category category : Category.values()) {
                sum_mask |= category.mask;
            }
            return sum_mask;
        }

        private short allExceptMe() {
            return (short) (all() ^ mask);
        }

        private short mask;
    }

    private void setFilter(Body body, short categoryBits, short maskBits){
        if (body != null) {
            Array<Fixture> fixtures = body.getFixtureList();
            Filter filter = new Filter();
            filter.categoryBits = categoryBits;
            filter.maskBits = maskBits;
            for (Fixture fixture : fixtures) {
                fixture.setFilterData(filter);
            }
        }
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

        FixtureDef fixtureDef = newStoneFixture(circleShape);
        boxBody.createFixture(fixtureDef);
        circleShape.dispose();

        setFilter(boxBody, Category.OTHER.mask, Category.all());
        return boxBody;
    }

    public Body newPlayerBody(Vector2 center, float radius) {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = BodyDef.BodyType.DynamicBody;
        boxBodyDef.position.x = center.x;
        boxBodyDef.position.y = center.y;
        boxBodyDef.fixedRotation = true;
        boxBodyDef.linearVelocity.y = 0f;
        boxBodyDef.angle = 0;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        FixtureDef fixtureDef = newStoneFixture(circleShape);
        fixtureDef.density = 100f;
        boxBody.createFixture(fixtureDef);
        circleShape.dispose();

        setFilter(boxBody, Category.PLAYER.mask, (short)(Category.PLAYER.allExceptMe() & ~Category.PLAYER.mask));
        return boxBody;
    }

    public Body newRectangleBody(Vector2 leftDownCorner, float width, float height, BodyDef.BodyType bodyType, boolean fixedRotation) {

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
        boxBody.createFixture(newStoneFixture(poly));
        poly.dispose();

        setFilter(boxBody, Category.OTHER.mask, Category.all());
        return boxBody;
    }

    public Body newMirror(Vector2 center, float width, float height) {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = BodyDef.BodyType.StaticBody;

        boxBodyDef.position.x = center.x;
        boxBodyDef.position.y = center.y;

        Body body = world.createBody(boxBodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        body.createFixture(newMirrorFixture(polygonShape));
        polygonShape.dispose();

        setFilter(body, Category.OTHER.mask, Category.all());
        return body;
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
        boxBody.createFixture(newStoneFixture(polygon));
        polygon.dispose();

        setFilter(boxBody, Category.OTHER.mask, Category.all());
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
            boxBody.createFixture(newSensorFixture(polygon));
            polygon.dispose();
        }

        return boxBody;
    }

    public Body newBullet(Vector2 source, Vector2 direction) {

        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = BodyDef.BodyType.DynamicBody;
        boxBodyDef.position.x = source.x;
        boxBodyDef.position.y = source.y;
        boxBodyDef.linearVelocity.x = 1e9f * direction.x;
        boxBodyDef.linearVelocity.y = 1e9f * direction.y;
        boxBodyDef.bullet = true;
        boxBodyDef.linearDamping = 0f;
        boxBodyDef.gravityScale = 0;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(RenderingSystem.getScreenSizeInMeters().x * 0.0005f);

        FixtureDef fixtureDef = newBouncingBulletFixture(circleShape);
        boxBody.createFixture(fixtureDef);
        circleShape.dispose();

        setFilter(boxBody, Category.BULLET.mask, (short)(Category.BULLET.allExceptMe() & ~Category.PLAYER.mask));
        return boxBody;
    }


    private static class BodyBuilder {
        BodyDef bodyDef = new BodyDef();

        Body
    }

    /**
     * Utility class for creating fixtures.
     */
    private static class FixtureFactory {

        private static FixtureDef newMirrorFixture(Shape shape) {
            return (new FixtureBuilder()).setShape(shape).setDensiity(0.5f)
                    .setFriction(0.2f).setRestitution(0.01f).build();
        }

        private static FixtureDef newBouncingBulletFixture(Shape shape) {
            return (new FixtureBuilder()).setShape(shape).setDensiity(0)
                    .setFriction(0).setRestitution(1).build();
        }

        private static FixtureDef newStoneFixture(Shape shape) {
            return (new FixtureBuilder()).setShape(shape).setDensiity(1)
                    .setFriction(0.9f).setRestitution(0.01f).build();
        }

        private static FixtureDef newSensorFixture(Shape shape){
            return (new FixtureBuilder()).setShape(shape).setSensor(true).build();
        }

        private static class FixtureBuilder {
            FixtureDef fixtureDef = new FixtureDef();

            FixtureDef build() {
                return fixtureDef;
            }

            FixtureBuilder setShape(Shape shape) {
                fixtureDef.shape = shape;
                return this;
            }

            FixtureBuilder setDensiity(float density) {
                fixtureDef.density = density;
                return this;
            }

            FixtureBuilder setFriction(float friction) {
                fixtureDef.friction = friction;
                return this;
            }

            FixtureBuilder setRestitution(float restitution) {
                fixtureDef.restitution = restitution;
                return this;
            }

            FixtureBuilder setSensor(boolean isSensor) {
                fixtureDef.isSensor = isSensor;
                return this;
            }
        }
    }
}
