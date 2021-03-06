package ru.hse.team.game.levels.TestShooting;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class ShootingLevelFactory extends AbstractLevelFactory {
    public ShootingLevelFactory(
        PooledEngine engine,
        KittensAssetManager manager,
        BodyFactory bodyFactory
    ) {
        super(engine, manager, bodyFactory);
    }

    @Override
    public void createLevel(int widthInScreens, int heightInScreens, AbstractLevel abstractLevel) {
        float width = RenderingSystem.getScreenSizeInMeters().x;
        float height = RenderingSystem.getScreenSizeInMeters().y;

        createBackground(widthInScreens, heightInScreens);

        abstractLevel.setPlayer(
            createPlayer(
                RenderingSystem.getScreenSizeInMeters().x / 2,
                RenderingSystem.getScreenSizeInMeters().y * 0.1f,
                RenderingSystem.getScreenSizeInMeters().x / 10f
            )
        );

        createMirror(
            new Vector2(0, 0.5f * height), 0.1f * width, height
        ); // left wall
        createMirror(
            new Vector2(width, 0.5f * height), 0.1f * width, height
        ); // right wall
        createMirror(
            new Vector2(0.5f * width, 0), width, 0.1f * height
        ); // down wall
        createMirror(
            new Vector2(0.5f * width, height), width, 0.1f * height
        ); // up wall
        createMirror(
            new Vector2(0.5f * width, 0.7f * height), 0.5f * width, 0.02f * height
        ); // obstacle

        createStar(0.5f * width, 0.85f * height, 0.05f * height);
        createStar(0.25f * width, 0.45f * height, 0.05f * height);
        createStar(0.75f * width, 0.45f * height, 0.05f * height);
    }
}
