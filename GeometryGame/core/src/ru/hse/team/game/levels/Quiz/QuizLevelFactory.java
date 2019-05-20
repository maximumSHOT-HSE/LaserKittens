package ru.hse.team.game.levels.Quiz;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.components.TransformComponent;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class QuizLevelFactory extends AbstractLevelFactory {

    private float CW;
    private float CH;

    public QuizLevelFactory() {
        world = new World(new Vector2(0, 0), true);
    }

    @Override
    public World getWorld() {
        return world;
    }

    private Entity placeImpenetrableWall(
            float relativeX,
            float relativeY,
            float relativeWidth,
            float relativeHeight) {
        return createImpenetrableWall(
            new Vector2(
                relativeX * RenderingSystem.getScreenSizeInMeters().x * CW,
                    relativeY * RenderingSystem.getScreenSizeInMeters().y * CH
            ),
            relativeWidth * RenderingSystem.getScreenSizeInMeters().x,
                relativeHeight * RenderingSystem.getScreenSizeInMeters().y
        );
    }

    private Entity placeTransparentWall(
            float relativeX,
            float relativeY,
            float relativeWidth,
            float relativeHeight) {
        return createTransparentWall(
                new Vector2(
                        relativeX * RenderingSystem.getScreenSizeInMeters().x * CW,
                        relativeY * RenderingSystem.getScreenSizeInMeters().y * CH
                ),
                relativeWidth * RenderingSystem.getScreenSizeInMeters().x,
                relativeHeight * RenderingSystem.getScreenSizeInMeters().y
        );
    }

    private Entity placePointer(
            float relativeX,
            float relativeY,
            float rotation) {
        return createPointer(
            new Vector2(
                relativeX * RenderingSystem.getScreenSizeInMeters().x * CW,
                    relativeY * RenderingSystem.getScreenSizeInMeters().y * CH
            ),
            rotation
        );
    }

    private Entity placeQuestion(float relativeX, float relativeY, float scale) {
        return createQuestion(new Vector2(
                relativeX * RenderingSystem.getScreenSizeInMeters().x * CW,
                relativeY * RenderingSystem.getScreenSizeInMeters().y * CH),
                scale);
    }

    private void createBorders() {
        placeImpenetrableWall(0, 0.5f, 0.2f, CH);
        placeImpenetrableWall(1, 0.5f, 0.2f, CH);
        placeImpenetrableWall(0.5f, 0, 1.025f * CW, 0.2f);
        placeImpenetrableWall(0.5f, 1, 1.025f * CW, 0.2f);
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        CH = getLevelHeightInScreens();
        CW = getLevelWidthInScreens();

        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();

        focusedPlayer = createPlayer(
                RenderingSystem.getScreenSizeInMeters().x * 3.5f,
                RenderingSystem.getScreenSizeInMeters().y * 0.1f,
                3f
        );

        createBorders();

        placeImpenetrableWall(3 / CW, 0.5f / CH, 0.1f, 1);
        placeImpenetrableWall(4 / CW, 0.5f / CH, 0.1f, 1);
        placeImpenetrableWall(3.15f / CW, 1f / CH, 0.4f, 0.1f);
        placeImpenetrableWall(3.85f / CW, 1f / CH, 0.4f, 0.1f);

        createStar(
            RenderingSystem.getScreenSizeInMeters().x * 3.5f,
            RenderingSystem.getScreenSizeInMeters().y,
            4
        );

        createStar(
                RenderingSystem.getScreenSizeInMeters().x * 3.5f,
                RenderingSystem.getScreenSizeInMeters().y * 4f,
                4
        );

        placePointer(3.5f / CW, 1.25f / CH, 90f);

        placeImpenetrableWall(3.5f / CW, 1.5f / CH, 1.1f, 0.1f);
        placeImpenetrableWall(2f / CW, 2f / CH, 0.1f, 1);
        placeImpenetrableWall(3f / CW, 2f / CH, 0.1f, 1);
        placeImpenetrableWall(2.5f / CW, 2.45f / CH, 1, 0.1f);
        placeTransparentWall(2.5f / CW, 1.7f / CH, 1, 0.1f);

        createStar(
            2.5f * RenderingSystem.getScreenSizeInMeters().x,
            2.35f * RenderingSystem.getScreenSizeInMeters().y,
            2f
        );

        createStar(
                2.25f * RenderingSystem.getScreenSizeInMeters().x,
                2.35f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );

        createStar(
                2.75f * RenderingSystem.getScreenSizeInMeters().x,
                2.35f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );

        placePointer(3.5f / CW, 0.5f / CH, 0f);

        placeQuestion(3.15f / CW, 0.9f / CH, 2f);
        placeQuestion(2.15f / CW, 1.6f / CH, 2f);
    }
}
