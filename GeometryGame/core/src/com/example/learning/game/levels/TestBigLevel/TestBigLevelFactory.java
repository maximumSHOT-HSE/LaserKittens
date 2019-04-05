package com.example.learning.game.levels.TestBigLevel;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.KittensAssetManager;
import com.example.learning.game.BodyFactory;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestBigLevelFactory extends AbstractLevelFactory {

    public TestBigLevelFactory() {
        world = new World(new Vector2(0,0), true);
    }

    @Override
    public World getWorld() {
        return world;
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

        focusedPlayer = createPlayer(RenderingSystem.getScreenSizeInMeters().x / 2, RenderingSystem.getScreenSizeInMeters().y * 0.1f,
                RenderingSystem.getScreenSizeInMeters().x / 10f);

        createMirror(new Vector2(0, 0.5f * levelHeight), 0.1f * screenWidth, levelHeight); // left wall
        createMirror(new Vector2(levelWidth, 0.5f * levelHeight), 0.1f * screenWidth, levelHeight); // right wall
        createMirror(new Vector2(0.5f * levelWidth, 0), levelWidth, 0.1f * screenHeight); // down wall
        createMirror(new Vector2(0.5f * levelWidth, levelHeight), levelWidth, 0.1f * screenHeight); // up wall

        createStar(0.5f * screenWidth, 0.85f * screenHeight, 0.05f * screenHeight);
    }
}
