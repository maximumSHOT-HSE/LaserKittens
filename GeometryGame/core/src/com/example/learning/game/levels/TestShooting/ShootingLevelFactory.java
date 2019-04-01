package com.example.learning.game.levels.TestShooting;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.MyAssetManager;
import com.example.learning.game.BodyFactory;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.TextureComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevelFactory;

public class ShootingLevelFactory extends AbstractLevelFactory {

    private Entity player;

    public ShootingLevelFactory() {
        world = new World(new Vector2(0,0), false);
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
        texture.region = new TextureRegion(manager.manager.get("Cat1.png", Texture.class));

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
        float width = RenderingSystem.getScreenSizeInMeters().x;
        float height = RenderingSystem.getScreenSizeInMeters().y;

        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();

        player = createPlayer(RenderingSystem.getScreenSizeInMeters().x / 2, RenderingSystem.getScreenSizeInMeters().y * 0.1f, 0.8f);

        createMirror(new Vector2(0, 0.5f * height), 0.1f * width, height); // left wall
        createMirror(new Vector2(width, 0.5f * height), 0.1f * width, height); // right wall
        createMirror(new Vector2(0.5f * width, 0), width, 0.1f * height); // down wall
        createMirror(new Vector2(0.5f * width, height), width, 0.1f * height); // up wall
        createMirror(new Vector2(0.5f * width, 0.7f * height), 0.5f * width, 0.02f * height); // obstacle

        createStar(0.25f * width, 0.43f * height, 0.05f * height);
        createStar(0.25f * width, 0.2f * height, 0.05f * height);
    }
}
