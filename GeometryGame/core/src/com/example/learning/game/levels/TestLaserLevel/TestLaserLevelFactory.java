package com.example.learning.game.levels.TestLaserLevel;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.MyAssetManager;
import com.example.learning.game.BodyFactory;
import com.example.learning.game.Mapper;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.TextureComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestLaserLevelFactory extends AbstractLevelFactory {

    private Entity player;

    public TestLaserLevelFactory() {
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
    public void createLevel(PooledEngine engine, MyAssetManager assetManager) {
        float width = RenderingSystem.getScreenSizeInMeters().x;
        float height = RenderingSystem.getScreenSizeInMeters().y;

        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();

        player = createPlayer(RenderingSystem.getScreenSizeInMeters().x / 2, RenderingSystem.getScreenSizeInMeters().y * 0.1f, 0.8f);

        createMirror(new Vector2(0, height * 0.5f), width * 0.1f, height); // left wall
        createMirror(new Vector2(width, height * 0.5f), width * 0.1f, height); // right wall
        createMirror(new Vector2(0.5f * width, 0), width, 0.1f * height); // down wall
        createMirror(new Vector2(0.5f * width, height), width, 0.1f * height); // up wall
        createMirror(new Vector2(0.5f * width, 0.7f * height), 0.5f * width, 0.02f * height); // obstacle

        createLaser(
            new Vector2(
                Mapper.transformComponent.get(player).position.x,
                Mapper.transformComponent.get(player).position.y
            ),
            new Vector2(+1, +1),
            15000
        );
    }
}
