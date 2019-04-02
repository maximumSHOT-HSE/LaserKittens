package com.example.learning.game.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.MyAssetManager;
import com.example.learning.game.BodyFactory;
import com.example.learning.game.Mapper;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.BulletComponent;
import com.example.learning.game.gamelogic.components.PlayerComponent;
import com.example.learning.game.gamelogic.components.StateComponent;
import com.example.learning.game.gamelogic.components.TextureComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;
import com.example.learning.game.gamelogic.components.TypeComponent;
import com.example.learning.game.gamelogic.systems.RenderingSystem;

import java.lang.reflect.Type;

abstract public class AbstractLevelFactory {

    protected BodyFactory bodyFactory;
    protected World world;
    protected PooledEngine engine;
    protected MyAssetManager manager;

    abstract public World getWorld();

    abstract public Entity getPlayer();

    abstract public void createLevel(PooledEngine engine, MyAssetManager assetManager);

    public Entity createStar(float x, float y, float radius) {
        Entity entity = engine.createEntity();

        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TypeComponent typeComponent = engine.createComponent(TypeComponent.class);
        StateComponent stateComponent = engine.createComponent(StateComponent.class);

        body.body = bodyFactory.newStar(new Vector2(x, y), radius, BodyDef.BodyType.DynamicBody, false);
        for(Fixture fixture : body.body.getFixtureList()) {
            fixture.setUserData(entity);
        }

        position.position.set(x, y,0);
        body.body.setUserData(entity);

        typeComponent.type = TypeComponent.ObjectType.STAR;

        // add the components to the entity
        entity.add(body);
        entity.add(position);
        entity.add(typeComponent);
        entity.add(stateComponent);

        // add the entity to the engine
        engine.addEntity(entity);
        return entity;
    }

    protected void createLaser(Vector2 source, Vector2 direction, float lifeTime) {
        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        BulletComponent bulletComponent = engine.createComponent(BulletComponent.class);
        StateComponent stateComponent = engine.createComponent(StateComponent.class);
        TypeComponent typeComponent = engine.createComponent(TypeComponent.class);

        position.position.x = source.x;
        position.position.y = source.y;

        body.body = bodyFactory.newBullet(source, direction);

        bulletComponent.creationTime = System.currentTimeMillis();
        bulletComponent.lifeTime = lifeTime;
        bulletComponent.path.add(source);
        bulletComponent.player = getPlayer();

        stateComponent.set(StateComponent.State.NORMAL);
        for(Fixture fixture : body.body.getFixtureList()) {
            fixture.setUserData(entity);
        }

        typeComponent.type = TypeComponent.ObjectType.BULLET;

        entity.add(body);
        entity.add(position);
        entity.add(texture);
        entity.add(bulletComponent);
        entity.add(stateComponent);
        entity.add(typeComponent);

        engine.addEntity(entity);
    }

    protected Entity createBackground() {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        // create the data for the components and add them to the components

        texture.region = new TextureRegion(manager.manager.get("blue-background.jpg", Texture.class));

        position.position.set(
                RenderingSystem.getScreenSizeInMeters().x / 2,
                RenderingSystem.getScreenSizeInMeters().y / 2,
                -1e9f
        );

        position.scale.set(
                RenderingSystem.getScreenSizeInPixels().x / texture.region.getRegionWidth(),
                RenderingSystem.getScreenSizeInPixels().y / texture.region.getRegionHeight()
        );

        // add the components to the entity
        entity.add(position);
        entity.add(texture);

        // add the entity to the engine
        engine.addEntity(entity);
        return entity;
    }

    protected void createMirror(Vector2 center, float width, float height) {

        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);

        position.position.x = center.x;
        position.position.y = center.y;
        body.body = bodyFactory.newMirror(center, width, height);

        entity.add(body);
        entity.add(position);
        entity.add(texture);

        engine.addEntity(entity);
    }

    protected Entity createPlayer(float px, float py, float radius) {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
        StateComponent stateComponent = engine.createComponent(StateComponent.class);
        // create the data for the components and add them to the components

        body.body = bodyFactory.newPlayerBody(new Vector2(px, py), radius);

        position.position.x = px;
        position.position.y = py;
        position.scale.set(0.2f, 0.2f);
        texture.region = new TextureRegion(manager.manager.get("Cat1.png", Texture.class));

        body.body.setUserData(entity);

        // add the components to the entity
        entity.add(body);
        entity.add(position);
        entity.add(texture);
        entity.add(playerComponent);
        entity.add(stateComponent);

        // add the entity to the engine
        engine.addEntity(entity);
        return entity;
    }

    public void shoot(float x, float y) {
        Vector3 playerPosition = Mapper.transformComponent.get(getPlayer()).position;

        Vector2 direction = new Vector2(x - playerPosition.x, y - playerPosition.y);
        float length = (float)Math.sqrt(direction.x * direction.x + direction.y * direction.y);
        direction.set(direction.x / length, direction.y / length);
        createLaser(
                new Vector2(playerPosition.x + (getPlayerRadius() + 0.01f) * direction.x,
                        playerPosition.y + (getPlayerRadius() + 0.01f) * direction.y),
                direction,
                1500
        );
    }

    // TODO Awful function. something to be done
    private float getPlayerRadius() {
        Body playerBody = Mapper.bodyComponent.get(getPlayer()).body;
        return playerBody.getFixtureList().get(0).getShape().getRadius();
    }
}
