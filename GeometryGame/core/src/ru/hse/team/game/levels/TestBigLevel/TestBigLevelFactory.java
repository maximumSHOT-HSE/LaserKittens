package ru.hse.team.game.levels.TestBigLevel;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class TestBigLevelFactory extends AbstractLevelFactory {
    public TestBigLevelFactory(
        PooledEngine engine,
        KittensAssetManager manager,
        BodyFactory bodyFactory
    ) {
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
                RenderingSystem.getScreenSizeInMeters().x / 2,
                RenderingSystem.getScreenSizeInMeters().y * 0.1f,
                RenderingSystem.getScreenSizeInMeters().x / 10f
            )
        );

        createMirror(
            new Vector2(0, 0.5f * levelHeight), 0.1f * screenWidth, levelHeight
        ); // left wall
        createMirror(
            new Vector2(levelWidth, 0.5f * levelHeight), 0.1f * screenWidth, levelHeight
        ); // right wall
        createMirror(
            new Vector2(0.5f * levelWidth, 0), levelWidth, 0.1f * screenHeight
        ); // down wall
        createMirror(
            new Vector2(0.5f * levelWidth, levelHeight), levelWidth, 0.1f * screenHeight
        ); // up wall

        createStar(0.5f * screenWidth, 0.85f * screenHeight, 0.05f * screenHeight);
    }
}
