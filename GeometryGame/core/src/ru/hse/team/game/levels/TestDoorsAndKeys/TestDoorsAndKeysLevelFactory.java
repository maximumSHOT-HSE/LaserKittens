package ru.hse.team.game.levels.TestDoorsAndKeys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class TestDoorsAndKeysLevelFactory extends AbstractLevelFactory {
    public TestDoorsAndKeysLevelFactory(
        PooledEngine engine,
        KittensAssetManager manager,
        BodyFactory bodyFactory) {
        super(engine, manager, bodyFactory);
    }

    @Override
    public void createLevel(int widthInScreens, int heightInScreens, AbstractLevel abstractLevel) {
        float screenWidth = RenderingSystem.getScreenSizeInMeters().x;
        float screenHeight = RenderingSystem.getScreenSizeInMeters().y;

        float levelWidth = screenWidth * widthInScreens;
        float levelHeight = screenHeight * heightInScreens;

        createBackground(widthInScreens, heightInScreens);
        abstractLevel.setPlayer(
            createPlayer(
                20f,
                20f,
                RenderingSystem.getScreenSizeInMeters().x / 10f
            )
        );
        createImpenetrableWall(
            new Vector2(0, 0.5f * levelHeight), 0.1f * screenWidth, levelHeight
        );
        createImpenetrableWall(
            new Vector2(levelWidth, 0.5f * levelHeight), 0.1f * screenWidth, levelHeight
        );
        createImpenetrableWall(
            new Vector2(levelWidth * 0.5f, 0), levelWidth, 0.1f * screenHeight
        );
        createImpenetrableWall(
            new Vector2(levelWidth * 0.5f, levelHeight), levelWidth, 0.1f * screenHeight
        );

        createImpenetrableWall(
            new Vector2(0.25f * screenWidth,0.25f * screenHeight), 0.5f * screenWidth, 0.1f * screenHeight
        );
        createStar(0.25f * screenWidth, 0.125f * screenHeight, 0.05f * screenHeight);

        Entity door = createDoor(
            new Vector2(0.5f * screenWidth, 0.15f * screenHeight),
            0.1f * screenWidth,
            0.3f * screenHeight
        );

        createKey(
            new Vector2(levelWidth - 0.5f * screenWidth, 0.5f * screenHeight),
            0.1f * screenWidth,
            0.2f * screenHeight,
            door
        );
    }
}

