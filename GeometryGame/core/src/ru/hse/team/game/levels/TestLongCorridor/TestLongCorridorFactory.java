package ru.hse.team.game.levels.TestLongCorridor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class TestLongCorridorFactory extends AbstractLevelFactory {

    private Entity player;

    public TestLongCorridorFactory() {
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

        createStar(0.5f * screenWidth, 0.25f * screenHeight, 0.05f * screenHeight);

        for (int i = 0; i < 8; i++) {
            createDisappearingWall(new Vector2(0.5f * screenWidth, (i + 1) * 0.5f * screenHeight), levelWidth, 0.1f * screenHeight);
            createStar(0.5f * screenWidth, (i + 1.5f) * 0.5f * screenHeight, 0.05f * screenHeight);
        }
    }
}
