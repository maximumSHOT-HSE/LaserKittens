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
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.TextureComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestLaserLevelFactory extends AbstractLevelFactory {

    private BodyFactory bodyFactory;
    public World world;
    private Entity player;

    public TestLaserLevelFactory() {
        world = new World(new Vector2(0,-10f), true);
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
        return player;
    }

    private Entity createPlayer() {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        // create the data for the components and add them to the components
        body.body = bodyFactory.newCircleBody(new Vector2(16f, 16f), 0.5f, BodyDef.BodyType.KinematicBody, false);

        position.position.set(16f, 16f,0);
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

    @Override
    public void createLevel(PooledEngine engine, MyAssetManager assetManager) {
        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();
        player = createPlayer();
    }
}
