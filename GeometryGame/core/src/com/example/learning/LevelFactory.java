package com.example.learning;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.gamelogic.components.BodyComponent;
import com.example.learning.gamelogic.components.TextureComponent;
import com.example.learning.gamelogic.components.TransformComponent;

public class LevelFactory {

    private BodyFactory bodyFactory;
    public World world;
    private PooledEngine engine;
    private MyAssetManager manager;

    public LevelFactory(PooledEngine en, MyAssetManager manager){
        engine = en;
        this.manager = manager;
        world = new World(new Vector2(0,-10f), true);
        bodyFactory = BodyFactory.getBodyFactory(world);
    }


    public void createPlayer(){

        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        // create the data for the components and add them to the components
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        body.body = bodyFactory.newCircleBody(new Vector2(0.5f * width, 0.2f * height), 150f, BodyDef.BodyType.KinematicBody, false);

        position.position.set(10,10,0);
        texture.region = new TextureRegion(manager.manager.get("badlogic.jpg", Texture.class));
        body.body.setUserData(entity);

        // add the components to the entity
        entity.add(body);
        entity.add(position);
        entity.add(texture);

        // add the entity to the engine
        engine.addEntity(entity);

    }
}
