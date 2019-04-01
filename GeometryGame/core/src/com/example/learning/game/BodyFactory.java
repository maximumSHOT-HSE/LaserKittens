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

    private BodyFactory(World world) {
        this.world = world;
    }

    public static BodyFactory getBodyFactory(World world) {
        return new BodyFactory(world);
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

        Body body = (new BodyBuilder()).setType(bodyType).setPosition(center)
                .setFixedRotation(fixedRotation).build();

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        body.createFixture(FixtureFactory.StoneFixture(circleShape));
        circleShape.dispose();

        setFilter(body, Category.OTHER.mask, Category.all());
        return body;
    }

    public Body newPlayerBody(Vector2 center, float radius) {
        Body body = (new BodyBuilder()).setType(BodyDef.BodyType.DynamicBody)
                .setPosition(center).setFixedRotation(true).build();

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        body.createFixture(FixtureFactory.PLayerFixture(circleShape));
        circleShape.dispose();

        setFilter(body, Category.PLAYER.mask, (short)(Category.PLAYER.allExceptMe() & ~Category.PLAYER.mask));
        return body;
    }

    public Body newRectangleBody(Vector2 leftDownCorner, float width, float height, BodyDef.BodyType bodyType, boolean fixedRotation) {

        Body body = (new BodyBuilder()).setType(bodyType)
                .setPosition(leftDownCorner.x + width / 2f, leftDownCorner.y + height / 2f)
                .setFixedRotation(fixedRotation).build();

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(width, height);
        body.createFixture(FixtureFactory.StoneFixture(poly));
        poly.dispose();

        setFilter(body, Category.OTHER.mask, Category.all());
        return body;
    }

    public Body newMirror(Vector2 center, float width, float height) {
        Body body = (new BodyBuilder()).setType(BodyDef.BodyType.StaticBody)
                .setPosition(center).build();

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        body.createFixture(FixtureFactory.MirrorFixture(polygonShape));
        polygonShape.dispose();

        setFilter(body, Category.OTHER.mask, Category.all());
        return body;
    }

    public Body newPolygonBody(Vector2[] polygonVertices, Vector2 leftDownCorner, BodyDef.BodyType bodyType, boolean fixedRotation) {

        Body body = (new BodyBuilder()).setType(bodyType).setPosition(leftDownCorner)
                .setFixedRotation(fixedRotation).build();

        PolygonShape polygon = new PolygonShape();
        polygon.set(polygonVertices);
        body.createFixture(FixtureFactory.StoneFixture(polygon));
        polygon.dispose();

        setFilter(body, Category.OTHER.mask, Category.all());
        return body;
    }

    private Vector2 coordByAngle(Vector2 center, float angle, float length) {
        return new Vector2(center.x + (float)Math.cos(Math.toRadians(angle)) * length,
                center.y + (float)Math.sin(Math.toRadians(angle)) * length);
    }

    public Body newStar(Vector2 center, float radius, BodyDef.BodyType bodyType, boolean fixedRotation) {

        Vector2 leftDownCorner = coordByAngle(center, 234f, radius);

        Body body = (new BodyBuilder()).setType(bodyType).setFixedRotation(fixedRotation)
                .setPosition(leftDownCorner).build();

        for (int i = 0; i < 5; i++) {
            float angle = 234f - i * 72f;
            float angleR = angle - 36f;
            float angleL = angle + 36f;

            Vector2[] coordinates = {coordByAngle(center, angle, radius), coordByAngle(center, angleR, radius / 2),
                    center, coordByAngle(center, angleL, radius / 2)};

            PolygonShape polygon = new PolygonShape();
            polygon.set(coordinates);
            body.createFixture(FixtureFactory.newSensorFixture(polygon));
            polygon.dispose();
        }

        setFilter(body, Category.OTHER.mask, Category.all());
        return body;
    }

    public Body newBullet(Vector2 source, Vector2 direction) {

        Body boxBody  = (new BodyBuilder()).setType(BodyDef.BodyType.DynamicBody).setPosition(source)
                .setFixedRotation(true).setLinearVelocity(1e9f * direction.x, 1e9f * direction.y)
                .setIsBullet(true).setLinearDamping(0).setGravityScale(0).build();

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(RenderingSystem.getScreenSizeInMeters().x * 0.0005f);
        FixtureDef fixtureDef = FixtureFactory.BouncingBulletFixture(circleShape);
        boxBody.createFixture(fixtureDef);
        circleShape.dispose();

        setFilter(boxBody, Category.BULLET.mask, (short)(Category.BULLET.allExceptMe() & ~Category.PLAYER.mask));
        return boxBody;
    }


    private class BodyBuilder {
        private BodyDef bodyDef = new BodyDef();

        private Body build() {
            return world.createBody(bodyDef);
        }

        private BodyBuilder setType(BodyDef.BodyType bodyType) {
            bodyDef.type = bodyType;
            return this;
        }

        private BodyBuilder setPosition(Vector2 position) {
            bodyDef.position.set(position);
            return this;
        }

        private BodyBuilder setPosition(float x, float y) {
            bodyDef.position.set(x, y);
            return this;
        }

        private BodyBuilder setLinearVelocity(Vector2 linearVelocity) {
            bodyDef.linearVelocity.set(linearVelocity);
            return this;
        }

        private BodyBuilder setLinearVelocity(float x, float y) {
            bodyDef.linearVelocity.set(x, y);
            return this;
        }

        private BodyBuilder setIsBullet(boolean isBullet) {
            bodyDef.bullet = true;
            return this;
        }

        private BodyBuilder setLinearDamping(float linearDamping) {
            bodyDef.linearDamping = linearDamping;
            return this;
        }

        private BodyBuilder setGravityScale(float gravityScale) {
            bodyDef.gravityScale = gravityScale;
            return this;
        }

        private BodyBuilder setFixedRotation(boolean fixedRotation) {
            bodyDef.fixedRotation = fixedRotation;
            return this;
        }
    }

    /**
     * Utility class for creating fixtures.
     */
    private static class FixtureFactory {

        private static FixtureDef MakeFixture(Shape shape, float density, float friction,
                                              float restitution) {
            return (new FixtureBuilder()).setShape(shape).setDensiity(density)
                    .setFriction(friction).setRestitution(restitution).build();
        }

        private static FixtureDef MirrorFixture(Shape shape) {
            return (new FixtureBuilder()).setShape(shape).setDensiity(0.5f)
                    .setFriction(0.2f).setRestitution(0.01f).build();
        }

        private static FixtureDef BouncingBulletFixture(Shape shape) {
            return (new FixtureBuilder()).setShape(shape).setDensiity(0)
                    .setFriction(0).setRestitution(1).build();
        }

        private static FixtureDef StoneFixture(Shape shape) {
            return (new FixtureBuilder()).setShape(shape).setDensiity(1)
                    .setFriction(0.9f).setRestitution(0.01f).build();
        }

        private static FixtureDef PLayerFixture(Shape shape) {
            return (new FixtureBuilder()).setShape(shape).setDensiity(100)
                    .setFriction(0.9f).setRestitution(0.1f).build();
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
