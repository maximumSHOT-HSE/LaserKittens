package com.example.learning.game.levels.TestStars;

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

public class TestStarsLevelFactory extends AbstractLevelFactory {

    private Entity player;

    public TestStarsLevelFactory(){
        world = new World(new Vector2(0,0), false);
    }

    public Entity createBackground() {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        // create the data for the components and add them to the components
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

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

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Entity getPlayer() {
        return player;
    }

    private Entity createStar(float x, float y, float radius) {
        Entity entity = engine.createEntity();

        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);

        body.body = bodyFactory.newStar(new Vector2(x, y), radius, BodyDef.BodyType.DynamicBody, false);

        position.position.set(x, y,0);
        body.body.setUserData(entity);

        // add the components to the entity
        entity.add(body);
        entity.add(position);

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
        createStar(10f, 10f, 3f);
        player = createPlayer(20f, 20f, 5f);
    }
}
