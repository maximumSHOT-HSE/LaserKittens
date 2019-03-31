package com.example.learning.game.levels;

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

abstract public class AbstractLevelFactory {

    protected BodyFactory bodyFactory;
    protected World world;
    protected PooledEngine engine;
    protected MyAssetManager manager;

    abstract public World getWorld();

    abstract public Entity getPlayer();

    abstract public void createLevel(PooledEngine engine, MyAssetManager assetManager);

    protected Entity createPlayer(float x, float y, float radius) {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        // create the data for the components and add them to the components
        body.body = bodyFactory.newPlayerBody(new Vector2(x, y), radius);

        position.position.set(x, y,0);
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
}
