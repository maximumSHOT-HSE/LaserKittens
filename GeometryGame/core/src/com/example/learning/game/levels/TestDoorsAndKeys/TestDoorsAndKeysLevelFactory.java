package com.example.learning.game.levels.TestDoorsAndKeys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.BodyFactory;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevelFactory;


public class TestDoorsAndKeysLevelFactory extends AbstractLevelFactory {

    private Entity player;

    public TestDoorsAndKeysLevelFactory() {
        world = new World(new Vector2(0,0), true);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Entity getPlayer() {
        return player;
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        float screenWidth = RenderingSystem.getScreenSizeInMeters().x;
        float screenHeight = RenderingSystem.getScreenSizeInMeters().y;

        float levelWidth = screenWidth * widthInScreens;
        float levelHeight = screenHeight * heightInScreens;

        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();
        player = createPlayer(20f, 20f, RenderingSystem.getScreenSizeInMeters().x / 10f);
        createImpenetrableWall(new Vector2(0, 0.5f * levelHeight), 0.1f * screenWidth, levelHeight);
        createImpenetrableWall(new Vector2(levelWidth, 0.5f * levelHeight), 0.1f * screenWidth, levelHeight);
        createImpenetrableWall(new Vector2(levelWidth * 0.5f, 0), levelWidth, 0.1f * screenHeight);
        createImpenetrableWall(new Vector2(levelWidth * 0.5f, levelHeight), levelWidth, 0.1f * screenHeight);

        createImpenetrableWall(new Vector2(0.25f * screenWidth,0.25f * screenHeight), 0.5f * screenWidth, 0.1f * screenHeight);
        createStar(0.25f * screenWidth, 0.125f * screenHeight, 0.05f * screenHeight);

        Entity door = createDoor(new Vector2(0.5f * screenWidth, 0.15f * screenHeight), 0.1f * screenWidth, 0.3f * screenHeight);
        createKey(new Vector2(levelWidth - 0.5f * screenWidth, 0.5f * screenHeight), 0.1f * screenWidth, 0.2f * screenHeight, door);
    }
}

