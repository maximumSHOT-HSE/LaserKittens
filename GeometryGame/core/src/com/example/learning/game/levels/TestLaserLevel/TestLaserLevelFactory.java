package com.example.learning.game.levels.TestLaserLevel;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
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

public class TestLaserLevelFactory extends AbstractLevelFactory {

    private BodyFactory bodyFactory;
    public World world;
    private Entity player;

    public TestLaserLevelFactory() {
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
            player = createPlayer();
        }
        return player;
    }

    private Entity createPlayer() {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        // create the data for the components and add them to the components
        float radius = 0.8f;
        float px = RenderingSystem.getScreenSizeInMeters().x / 2;
        float py = RenderingSystem.getScreenSizeInMeters().y * 0.1f;
        body.body = bodyFactory.newCircleBody(new Vector2(px, py), radius, BodyDef.BodyType.KinematicBody, false);

        position.position.x = px;
        position.position.y = py;
        position.scale.set(0.05f, 0.05f);
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
        bulletComponent.lifeTime = 1000;

        stateComponent.set(StateComponent.State.NORMAL);



        entity.add(body);
        entity.add(position);
        entity.add(texture);
        entity.add(bulletComponent);
        entity.add(stateComponent);

        engine.addEntity(entity);
    }

    @Override
    public void createLevel(PooledEngine engine, MyAssetManager assetManager) {
        float width = RenderingSystem.getScreenSizeInMeters().x;
        float height = RenderingSystem.getScreenSizeInMeters().y;

        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();
        player = createPlayer();

        createWall(new Vector2(0, height * 0.5f), width * 0.1f, height); // left wall
        createWall(new Vector2(width, height * 0.5f), width * 0.1f, height); // right wall
        createWall(new Vector2(0.5f * width, 0), width, 0.1f * height); // down wall
        createWall(new Vector2(0.5f * width, height), width, 0.1f * height); // up wall
        createWall(new Vector2(0.5f * width, 0.7f * height), 0.5f * width, 0.02f * height);

        createLaser(
            new Vector2(
                Mapper.transformComponent.get(player).position.x,
                Mapper.transformComponent.get(player).position.y
            ),
            new Vector2(+1, +1)
        );
    }
}
