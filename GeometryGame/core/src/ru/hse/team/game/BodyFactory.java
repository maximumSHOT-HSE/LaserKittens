package ru.hse.team.game;

import android.support.annotation.Nullable;

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

import ru.hse.team.game.gamelogic.systems.RenderingSystem;

/**
 * Utility class for creating bodies.
 */
public class BodyFactory {
    private final World world;

    private BodyFactory(World world) {
        this.world = world;
    }

    public static BodyFactory getBodyFactory(World world) {
        return new BodyFactory(world);
    }

    /**
     * Enum specifying body type, which is necessary for filtering body collisions.
     * Box2D allows up to 16 body types
     */
    private enum Category {
        PLAYER(0),
        BULLET(1),
        OTHER(2),
        TRANSPARENT(3);

        private short mask;

        Category(int index) {
            this.mask = (short)(1 << index);
        }

        private static short all = 0;
        static {
            for (Category category : Category.values()) {
                all |= category.mask;
            }
        }

        private short allExceptMe() {
            return (short) (all ^ mask);
        }
    }

    /**
     * Goes though all fixtures in body and sets
     * them filter specified by given masks.
     */
    private void setFilter(@Nullable Body body, short categoryBits, short maskBits) {
        if (body == null) {
            return;
        }
        Array<Fixture> fixtures = body.getFixtureList();
        Filter filter = new Filter();
        filter.categoryBits = categoryBits;
        filter.maskBits = maskBits;
        for (Fixture fixture : fixtures) {
            fixture.setFilterData(filter);
        }
    }

    public Body newTransparentRectangle(
        Vector2 center,
        float width,
        float height,
        float rotation
    ) {
        Body body = newRectangle(center, width, height, rotation);
        setFilter(
            body,
            Category.TRANSPARENT.mask,
            (short) (Category.all ^ Category.BULLET.mask)
        );
        return body;
    }

    public Body newCircleBody(
        Vector2 center,
        float radius,
        BodyDef.BodyType bodyType,
        boolean fixedRotation
    ) {
        Body body = new BodyBuilder()
                .setType(bodyType)
                .setPosition(center)
                .setFixedRotation(fixedRotation)
                .build();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        body.createFixture(FixtureFactory.stoneFixture(circleShape));
        circleShape.dispose();
        setFilter(body, Category.OTHER.mask, Category.all);
        return body;
    }

    public Body newPlayerBody(Vector2 center, float radius) {
        Body body = new BodyBuilder()
                .setType(BodyDef.BodyType.DynamicBody)
                .setPosition(center)
                .setFixedRotation(true)
                .build();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        body.createFixture(FixtureFactory.playerFixture(circleShape));
        circleShape.dispose();
        setFilter(body, Category.PLAYER.mask, (short)(Category.all & ~Category.BULLET.mask));
        return body;
    }

    public Body newGuardianBody(Vector2 center, float radius) {
        Body body = new BodyBuilder()
                .setType(BodyDef.BodyType.KinematicBody)
                .setPosition(center)
                .setFixedRotation(false)
                .build();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        body.createFixture(FixtureFactory.playerFixture(circleShape));
        circleShape.dispose();
        setFilter(body, Category.OTHER.mask, Category.all);
        return body;
    }

    public Body newDynamicRectangle(Vector2 center, float width, float height, float rotation) {
        Body body = new BodyBuilder()
                .setType(BodyDef.BodyType.DynamicBody)
                .setPosition(center)
                .setRotation(rotation)
                .setGravityScale(10)
                .setLinearDamping(1f)
                .build();
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        body.createFixture(FixtureFactory.ignoringWallFixture(polygonShape));
        polygonShape.dispose();
        setFilter(body, Category.OTHER.mask, Category.all);
        return body;
    }

    public Body newRectangle(Vector2 center, float width, float height, float rotation) {
        Body body = new BodyBuilder()
                .setType(BodyDef.BodyType.KinematicBody)
                .setPosition(center)
                .setRotation(rotation)
                .build();
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        body.createFixture(FixtureFactory.mirrorFixture(polygonShape));
        polygonShape.dispose();
        setFilter(body, Category.OTHER.mask, Category.all);
        return body;
    }

    public Body newRectangle(Vector2 center, float width, float height) {
        return newRectangle(center, width, height, 0);
    }

    public Body newSensorRectangle(Vector2 center, float width, float height, float rotation) {
        Body body = new BodyBuilder()
                .setType(BodyDef.BodyType.StaticBody)
                .setPosition(center)
                .setRotation(rotation)
                .build();
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        body.createFixture(FixtureFactory.sensorFixture(polygonShape));
        polygonShape.dispose();
        setFilter(body, Category.OTHER.mask, Category.PLAYER.mask);
        return body;
    }

    private Vector2 coordinatesByAngle(Vector2 center, float angle, float length) {
        return new Vector2(
            center.x + (float)Math.cos(Math.toRadians(angle)) * length,
            center.y + (float)Math.sin(Math.toRadians(angle)) * length
        );
    }

    public Body newStar(Vector2 center, float radius, BodyDef.BodyType bodyType, boolean fixedRotation) {
        Vector2 origin = new Vector2(0, 0);
        Body body = new BodyBuilder()
                .setType(bodyType)
                .setFixedRotation(fixedRotation)
                .setPosition(center)
                .build();

        int starRays = 5;
        float fullCircleDegrees = 360f;
        for (int i = 0; i < starRays; i++) {
            float angle = fullCircleDegrees * 0.6f - i * fullCircleDegrees / starRays;
            float angleR = angle - fullCircleDegrees / (2 * starRays);
            float angleL = angle + fullCircleDegrees / (2 * starRays);
            Vector2[] coordinates = {
                    coordinatesByAngle(origin, angle, radius),
                    coordinatesByAngle(origin, angleL, radius / 2),
                    origin,
                    coordinatesByAngle(origin, angleR, radius / 2)
            };
            PolygonShape polygon = new PolygonShape();
            polygon.set(coordinates);
            body.createFixture(FixtureFactory.sensorFixture(polygon));
            polygon.dispose();
        }
        setFilter(body, Category.OTHER.mask, Category.all);
        return body;
    }

    public Body newBullet(Vector2 source, Vector2 direction) {
        Body boxBody  = new BodyBuilder()
                .setType(BodyDef.BodyType.DynamicBody)
                .setPosition(source)
                .setFixedRotation(true)
                .setLinearVelocity(1e9f * direction.x, 1e9f * direction.y)
                .setIsBullet(true)
                .setLinearDamping(0)
                .setGravityScale(0)
                .build();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(RenderingSystem.getScreenSizeInMeters().x * 0.0005f);
        FixtureDef fixtureDef = FixtureFactory.bouncingBulletFixture(circleShape);
        boxBody.createFixture(fixtureDef);
        circleShape.dispose();
        setFilter(
            boxBody,
            Category.BULLET.mask,
            (short) (Category.BULLET.allExceptMe() & ~Category.PLAYER.mask)
        );
        return boxBody;
    }

    // adds bodies to world on build
    private class BodyBuilder {
        private final BodyDef bodyDef = new BodyDef();

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

        private BodyBuilder setRotation(float rotation) {
            bodyDef.angle = rotation;
            return this;
        }
    }

    /**
     * Utility class for creating fixtures.
     */
    private static class FixtureFactory {
        private static FixtureDef mirrorFixture(Shape shape) {
            return new FixtureBuilder()
                    .setShape(shape)
                    .setDensiity(0.5f)
                    .setFriction(0.2f)
                    .setRestitution(0.01f)
                    .build();
        }

        private static FixtureDef bouncingBulletFixture(Shape shape) {
            return new FixtureBuilder()
                    .setShape(shape)
                    .setDensiity(0)
                    .setFriction(0)
                    .setRestitution(1)
                    .build();
        }

        private static FixtureDef ignoringWallFixture(Shape shape) {
            return new FixtureBuilder()
                    .setShape(shape)
                    .setDensiity(200)
                    .setFriction(0.5f)
                    .setRestitution(0)
                    .build();
        }

        private static FixtureDef stoneFixture(Shape shape) {
            return new FixtureBuilder()
                    .setShape(shape)
                    .setDensiity(1)
                    .setFriction(0.9f)
                    .setRestitution(0.01f)
                    .build();
        }

        private static FixtureDef playerFixture(Shape shape) {
            return new FixtureBuilder()
                    .setShape(shape)
                    .setDensiity(100)
                    .setFriction(0.9f)
                    .setRestitution(0.1f)
                    .build();
        }

        private static FixtureDef sensorFixture(Shape shape){
            return new FixtureBuilder()
                    .setShape(shape)
                    .setSensor(true)
                    .build();
        }

        private static class FixtureBuilder {
            private final FixtureDef fixtureDef = new FixtureDef();

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
