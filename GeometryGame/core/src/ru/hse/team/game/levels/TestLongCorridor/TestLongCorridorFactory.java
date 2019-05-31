package ru.hse.team.game.levels.TestLongCorridor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class TestLongCorridorFactory extends AbstractLevelFactory {

    public TestLongCorridorFactory(PooledEngine engine, KittensAssetManager manager, BodyFactory bodyFactory) {
        super(engine, manager, bodyFactory);
    }

    @Override
    public void createLevel(int widthInScreens, int heightInScreens, AbstractLevel abstractLevel) {
        float screenWidth = RenderingSystem.getScreenSizeInMeters().x;
        float screenHeight = RenderingSystem.getScreenSizeInMeters().y;

        float levelWidth = screenWidth * widthInScreens;
        float levelHeight = screenHeight * heightInScreens;

        createBackground(widthInScreens, heightInScreens);
        abstractLevel.setPlayer(createPlayer(20f, 20f, RenderingSystem.getScreenSizeInMeters().x / 10f));
        createImpenetrableWall(new Vector2(0, 0.5f * levelHeight), 0.1f * screenWidth, levelHeight);
        createImpenetrableWall(new Vector2(levelWidth, 0.5f * levelHeight), 0.1f * screenWidth, levelHeight);
        createImpenetrableWall(new Vector2(levelWidth * 0.5f, 0), levelWidth, 0.1f * screenHeight);
        createImpenetrableWall(new Vector2(levelWidth * 0.5f, levelHeight), levelWidth, 0.1f * screenHeight);

        createStar(0.5f * screenWidth, 0.25f * screenHeight, 0.05f * screenHeight);

        for (int i = 0; i < 8; i++) {
            Entity door = createDoor(new Vector2(0.5f * screenWidth, (i + 1) * 0.5f * screenHeight), levelWidth, 0.1f * screenHeight);
            createKey(new Vector2(0.5f * screenWidth, (i + 1) * 0.5f * screenHeight), levelWidth, 0.1f * screenHeight, door);
            createStar(0.5f * screenWidth, (i + 1.5f) * 0.5f * screenHeight, 0.05f * screenHeight);
        }
    }
}
