package ru.hse.team.game.Multiplayer;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.TextureComponent;
import ru.hse.team.game.gamelogic.components.TumblerComponent;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class MultiplayerQuizLevelFactory extends AbstractLevelFactory {

    private float CW;
    private float CH;
    private int role = 1;

    private Entity opponentPlayer = null;

    public MultiplayerQuizLevelFactory(PooledEngine engine, KittensAssetManager manager, BodyFactory bodyFactory, int role) {
        super(engine, manager, bodyFactory);
        this.role = role;
    }

    @Override
    public void setOpponentPosition(Vector2 position) {
        if (opponentPlayer == null) {
            opponentPlayer = createPlayer(position.x, position.y, 3f);
        }
        BodyComponent bodyComponent = Mapper.bodyComponent.get(opponentPlayer);
        if (bodyComponent != null) {
            Body body = bodyComponent.body;
            if (body != null) {
                body.setTransform(position, 0);
            }
        }
    }

    private void createBorders() {
        placeImpenetrableWall(0, 0.5f * CH, 0.1f, CH);
        placeImpenetrableWall(CW, 0.5f * CH, 0.1f, CH);
        placeImpenetrableWall(0.5f * CW, 0, 1.025f * CW, 0.1f);
        placeImpenetrableWall(0.5f * CW, CH, 1.025f * CW, 0.1f);
    }

    @Override
    public void createLevel(int widthInScreens, int heightInScreens, AbstractLevel abstractLevel) {
        CH = widthInScreens;
        CW = heightInScreens;

        createBackground(widthInScreens, heightInScreens);

        if (role == 1) {
            abstractLevel.setPlayer(createPlayer(
                    RenderingSystem.getScreenSizeInMeters().x * 0.25f,
                    RenderingSystem.getScreenSizeInMeters().y * 0.8f,
                    3f));
        } else {
            abstractLevel.setPlayer(createPlayer(
                    RenderingSystem.getScreenSizeInMeters().x * 0.25f,
                    RenderingSystem.getScreenSizeInMeters().y * 1.8f,
                    3f));
        }

        createBorders();

        placeImpenetrableWall(1f, 1f, 2f, 0.1f);
        placeImpenetrableWall(1.5f, 1.4f, 0.1f, 0.8f);
        placeTransparentWall(1.5f, 1.9f, 0.05f, 0.2f);
        placeImpenetrableWall(0.5f, 0.25f, 0.05f, 0.5f);
        placeImpenetrableWall(1.75f, 0.5f, 0.5f, 0.05f);
        Entity barrier = placeDynamicImpenetrableWall(1.4f, 0.25f, 0.05f, 0.5f);
        Entity downDoor = placeDoor(0.25f, 0.45f, 0.5f, 0.025f);
        createKey(new Vector2(
                        1.75f * RenderingSystem.getScreenSizeInMeters().x,
                        1.1f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                downDoor);
        createStar(
                0.25f * RenderingSystem.getScreenSizeInMeters().x,
                1.2f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );
        placeImpenetrableWall(0.25f, 1.4f, 0.5f, 0.05f);
        Entity upDoor = placeDoor(0.45f, 1.2f, 0.05f, 0.4f);
        placeTransparentWall(1f, 0.5f, 0.2f, 1f);
        createKey(new Vector2(
                        1.75f * RenderingSystem.getScreenSizeInMeters().x,
                        0.4f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                upDoor);

        final Entity tumbler = createTumbler(new Vector2(
                        0.25f * RenderingSystem.getScreenSizeInMeters().x,
                        0.2f * RenderingSystem.getScreenSizeInMeters().y),
                0.07f * RenderingSystem.getScreenSizeInMeters().x,
                0.04f * RenderingSystem.getScreenSizeInMeters().y, () -> {});
        Runnable task = new Runnable() {

            private int state = 0;

            @Override
            public void run() {
                if (state == 0) {
                    barrier.getComponent(BodyComponent.class).body.setLinearVelocity(0, 5);
                    tumbler.getComponent(TextureComponent.class).region.setTexture(new Texture(KittensAssetManager.BLUE_TUMBLER));
                } else {
                    barrier.getComponent(BodyComponent.class).body.setLinearVelocity(0, -5);
                    tumbler.getComponent(TextureComponent.class).region.setTexture(new Texture(KittensAssetManager.YELLOW_TUMBLER));
                }
                state ^= 1;
            }
        };
        tumbler.getComponent(TumblerComponent.class).setAction(task);
        placeMirror(1.85f, 1.75f, 0.05f, 0.6f, 60);
    }
}
