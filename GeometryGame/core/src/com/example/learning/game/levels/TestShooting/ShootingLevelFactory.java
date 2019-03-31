package com.example.learning.game.levels.TestShooting;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.MyAssetManager;
import com.example.learning.game.BodyFactory;
import com.example.learning.game.Mapper;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.BulletComponent;
import com.example.learning.game.gamelogic.components.StateComponent;
import com.example.learning.game.gamelogic.components.TextureComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevelFactory;

import java.util.Map;

import javax.swing.Box;

public class ShootingLevelFactory extends AbstractLevelFactory {

    private Entity player;

    public ShootingLevelFactory() {
        world = new World(new Vector2(0,0), false);
    }

    public Entity createBackground() {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        // create the data for the components and add them to the components

        position.position.set(
                RenderingSystem.getScreenSizeInMeters().x / 2,
                RenderingSystem.getScreenSizeInMeters().y / 2,
                -1e9f
        );
        texture.region = new TextureRegion(manager.manager.get("blue-background.jpg", Texture.class));

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

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Entity getPlayer() {
        if (player == null) {
            player = createPlayer(RenderingSystem.getScreenSizeInMeters().x / 2, RenderingSystem.getScreenSizeInMeters().y * 0.1f, 0.8f);
        }
        return player;
    }

    @Override
    protected Entity createPlayer(float px, float py, float radius) {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        // create the data for the components and add them to the components

        body.body = bodyFactory.newPlayerBody(new Vector2(px, py), radius);

        position.position.x = px;
        position.position.y = py;
        position.scale.set(0.2f, 0.2f);
        texture.region = new TextureRegion(manager.manager.get("badlogic.jpg", Texture.class));

        body.body.setUserData(entity);

        // add the components to the entity
        entity.add(body);
        entity.add(position);
        entity.add(texture);

        // add the entity to the engine
        engine.addEntity(entity);
        return entity;
    }

    private void createWall(Vector2 center, float width, float height) {

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

    private void createLaser(Vector2 source, Vector2 direction) {

        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        BulletComponent bulletComponent = engine.createComponent(BulletComponent.class);
        StateComponent stateComponent = engine.createComponent(StateComponent.class);

        position.position.x = source.x;
        position.position.y = source.y;

        body.body = bodyFactory.newBullet(source, direction);

        bulletComponent.creationTime = System.currentTimeMillis();
        bulletComponent.lifeTime = 1500;
        bulletComponent.path.add(source);
        bulletComponent.player = player;

        stateComponent.set(StateComponent.State.NORMAL);
        for(Fixture fixture : body.body.getFixtureList()) {
            fixture.setUserData(entity);
        }

        entity.add(body);
        entity.add(position);
        entity.add(texture);
        entity.add(bulletComponent);
        entity.add(stateComponent);

        engine.addEntity(entity);
    }

    //TODO Awful function. something to be done
    private float getPlayerRadius() {
        Body playerBody = Mapper.bodyComponent.get(player).body;
        return playerBody.getFixtureList().get(0).getShape().getRadius();
    }

    @Override
    public void shoot(float x, float y) {
        Vector3 playerPosition = Mapper.transformComponent.get(player).position;

        Vector2 direction = new Vector2(x - playerPosition.x, y - playerPosition.y);
        float length = (float)Math.sqrt(direction.x * direction.x + direction.y * direction.y);
        direction.set(direction.x / length, direction.y / length);
        createLaser(
                new Vector2(playerPosition.x + (getPlayerRadius() + 0.01f) * direction.x,
                        playerPosition.y + (getPlayerRadius() + 0.01f) * direction.y),
                direction
        );
    }

    @Override
    public void createLevel(PooledEngine engine, MyAssetManager assetManager) {
        float width = RenderingSystem.getScreenSizeInMeters().x;
        float height = RenderingSystem.getScreenSizeInMeters().y;

        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();

        player = createPlayer(RenderingSystem.getScreenSizeInMeters().x / 2, RenderingSystem.getScreenSizeInMeters().y * 0.1f, 0.8f);

        createWall(new Vector2(0, height * 0.5f), width * 0.1f, height); // left wall
        createWall(new Vector2(width, height * 0.5f), width * 0.1f, height); // right wall
        createWall(new Vector2(0.5f * width, 0), width, 0.1f * height); // down wall
        createWall(new Vector2(0.5f * width, height), width, 0.1f * height); // up wall
        createWall(new Vector2(0.5f * width, 0.7f * height), 0.5f * width, 0.02f * height); // obstacle

    }
}
