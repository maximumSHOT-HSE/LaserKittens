package ru.hse.team.game.levels.Quiz;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.StateComponent;
import ru.hse.team.game.gamelogic.components.TextureComponent;
import ru.hse.team.game.gamelogic.components.TumblerComponent;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class QuizLevelFactory extends AbstractLevelFactory {

    private float CW;
    private float CH;

    public QuizLevelFactory(PooledEngine engine, KittensAssetManager manager, BodyFactory bodyFactory) {
        super(engine, manager, bodyFactory);
    }

    private void createBorders() {
        placeImpenetrableWall(0, 0.5f * CH, 0.2f, CH);
        placeImpenetrableWall(CW, 0.5f * CH, 0.2f, CH);
        placeImpenetrableWall(0.5f * CW, 0, 1.025f * CW, 0.2f);
        placeImpenetrableWall(0.5f * CW, CH, 1.025f * CW, 0.2f);
    }

    private void createSection1() {
        placeImpenetrableWall(3, 0.5f, 0.1f, 1);
        placeImpenetrableWall(4, 0.5f, 0.1f, 1);
        placeImpenetrableWall(3.15f, 1f, 0.4f, 0.1f);
        placeImpenetrableWall(3.85f, 1f, 0.4f, 0.1f);

        createStar(
                RenderingSystem.getScreenSizeInMeters().x * 3.5f,
                RenderingSystem.getScreenSizeInMeters().y,
                4
        );

        placePointer(3.5f, 1.25f, 90f);

        placeImpenetrableWall(3.5f, 1.5f, 1.1f, 0.1f);
        placeImpenetrableWall(2f, 2f, 0.1f, 1);
        placeImpenetrableWall(3f, 2f, 0.1f, 1);
        placeImpenetrableWall(2.5f, 2.45f, 1, 0.1f);
        placeTransparentWall(2.5f, 1.7f, 1, 0.1f);

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

        placePointer(3.5f, 0.5f, 0f);

        placeQuestion(3.15f, 0.9f, 2f, "A");
        placeQuestion(2.15f, 1.6f, 2f, "B");

        placeImpenetrableWall(1f, 1.5f, 2.1f, 0.05f);
        placeImpenetrableWall(2f, 1.2f, 0.1f, 1.7f);
        placeImpenetrableWall(1.25f, 0.35f, 1.6f, 0.05f);
        placeImpenetrableWall(0.45f, 0.8f, 0.1f, 0.95f);
        placeImpenetrableWall(1f, 1.25f, 1, 0.05f);
        placeTransparentWall(1f, 0.175f, 0.1f, 0.35f);

        placeQuestion(2f, 0.15f, 2, "C");

        placePointer(2.5f, 1f, 180);
        placePointer(2.5f, 0.4f, 135);

        placeMirror(0.2f, 0.2f, 0.05f, 0.4f, -40);
        placeMirror(0.2f, 1.355f, 0.05f, 0.4f, 40);
        placeMirror(1.89f, 1.31f, 0.05f, 0.4f, -50);
        addAngularVelocity(placeMirror(0.7f, 0.825f, 0.05f, 0.2f, 0), 1);

        createStar(
            1 * RenderingSystem.getScreenSizeInMeters().x,
            1f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );

        createStar(
            1 * RenderingSystem.getScreenSizeInMeters().x,
            0.65f * RenderingSystem.getScreenSizeInMeters().y,
            2f
        );

    }

    private void createSection2() {
        placeImpenetrableWall(3.25f, 2.45f, 0.5f, 0.1f);
        placeImpenetrableWall(4.25f, 2.45f, 0.5f, 0.1f);
        placeImpenetrableWall(4.75f, 1.5f, 0.5f, 0.1f);
        placeImpenetrableWall(4.75f, 1f, 0.5f, 0.1f);
        placeImpenetrableWall(4.5f, 1.975f, 0.1f, 1.05f);
        addAngularVelocity(placeImpenetrableWall(4.25f, 1.5f, 0.4f, 0.01f), 1);
        placeTransparentWall(4.25f, 1.5f, 0.4f, 0.1f);
        Entity rightDoor = placeDoor(4.9f, 1.25f, 0.1f, 0.5f);
        placeMirror(3.75f, 2.425f, 0.5f, 0.05f, 0);
        placeImpenetrableWall(3.75f, 2.475f, 0.5f, 0.05f);
        placeImpenetrableWall(4.75f, 1f, 0.5f, 0.1f);
        placeImpenetrableWall(4.25f, 0.75f, 0.6f, 0.05f);
        placeImpenetrableWall(4.5f, 0.9f, 0.1f, 0.3f);
        Entity downDoor = placeDoor(4.25f, 1f, 0.6f, 0.05f);
        createKey(new Vector2(
                3.2f * RenderingSystem.getScreenSizeInMeters().x,
                    2.1f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                downDoor);
        createKey(new Vector2(
                        4.4f * RenderingSystem.getScreenSizeInMeters().x,
                        2.375f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                downDoor);
        createKey(new Vector2(
                        3.1f * RenderingSystem.getScreenSizeInMeters().x,
                        1.585f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                downDoor);
        createKey(new Vector2(
                        4.25f * RenderingSystem.getScreenSizeInMeters().x,
                        0.9f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                rightDoor);
        placeQuestion(4.8f, 1.4f, 2, "D");
        placePointer(4.5f, 1.25f, -90);
    }

    private void createSection3() {
        Entity upDoor = placeDoor(5.25f, 1.5f, 0.5f, 0.05f);
        placeImpenetrableWall(5.5f, 1.15f, 0.1f, 0.8f);
        placeImpenetrableWall(5.25f, 0.75f, 0.6f, 0.05f);
        placeTransparentWall(4.75f, 0.75f, 0.5f, 0.05f);
        placeImpenetrableWall(5, 0.375f, 0.1f, 0.75f);
        createKey(new Vector2(
                        4.4f * RenderingSystem.getScreenSizeInMeters().x,
                        0.15f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                upDoor);
        createKey(new Vector2(
                        4.25f * RenderingSystem.getScreenSizeInMeters().x,
                        0.5f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                upDoor);
        final Entity movableWall = placeDynamicImpenetrableWall(4.5f, 0.375f, 0.05f, 0.5f);
        final Entity tumbler = createTumbler(new Vector2(
                5.4f * RenderingSystem.getScreenSizeInMeters().x,
                0.8f * RenderingSystem.getScreenSizeInMeters().y),
            0.07f * RenderingSystem.getScreenSizeInMeters().x,
            0.04f * RenderingSystem.getScreenSizeInMeters().y, () -> {});
        Runnable task = new Runnable() {

            private int state = 0;

            @Override
            public void run() {
                if (state == 0) {
                    movableWall.getComponent(BodyComponent.class).body.setLinearVelocity(0, 50);
                    tumbler.getComponent(TextureComponent.class).region.setTexture(getManager().getImage(KittensAssetManager.Images.BLUE_TUMBLER));
                } else {
                    movableWall.getComponent(BodyComponent.class).body.setLinearVelocity(0, -50);
                    tumbler.getComponent(TextureComponent.class).region.setTexture(getManager().getImage(KittensAssetManager.Images.YELLOW_TUMBLER));
                }
                state ^= 1;
            }
        };

        tumbler.getComponent(TumblerComponent.class).setAction(task);
        placeMirror(4.8f, 0.1f, 0.05f, 0.3f, -70);

        placeQuestion(4.6f, 0.9f, 2, "E");
        placePointer(5.25f, 1.25f, 180);
    }

    private void createSection4() {
        placeImpenetrableWall(5.75f, 2.45f, 2.5f, 0.1f);
        placeImpenetrableWall(7f, 1.625f, 0.1f, 0.25f);
        placeImpenetrableWall(7f, 2.375f, 0.1f, 0.25f);
        placeImpenetrableWall(6.25f, 1.5f, 1.6f, 0.1f);
        Entity door = placeDoor(7f, 2f, 0.1f, 0.5f);
        placeImpenetrableWall(5.5f, 1.75f, 1f, 0.03f);
        placeImpenetrableWall(6.25f, 2.25f, 0.5f, 0.03f);
        placeImpenetrableWall(5.25f, 2.25f, 0.5f, 0.03f);
        placeImpenetrableWall(6.5f, 2f, 0.05f, 0.5f);
        placeImpenetrableWall(5f, 1.875f, 0.03f, 0.25f);
        createKey(new Vector2(
                        5.075f * RenderingSystem.getScreenSizeInMeters().x,
                        1.8f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                door);
        createKey(new Vector2(
                        6.425f * RenderingSystem.getScreenSizeInMeters().x,
                        2.2f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                door);
        List<Vector2> path = new ArrayList<>();
        path.add(new Vector2(6.75f, 1.6f));
        path.add(new Vector2(4.75f, 1.6f));
        path.add(new Vector2(4.75f, 2.35f));
        path.add(new Vector2(6.75f, 2.35f));
        for (Vector2 vertex : path) {
            vertex.x *= RenderingSystem.getScreenSizeInMeters().x;
            vertex.y *= RenderingSystem.getScreenSizeInMeters().y;
        }
        createGuardian(
                RenderingSystem.getScreenSizeInMeters().x * 6.75f,
                RenderingSystem.getScreenSizeInMeters().y * 1.6f,
                3f, path, 30);
        path = new Vector<>(path);
        Collections.rotate(path, 2);
        createGuardian(
                RenderingSystem.getScreenSizeInMeters().x * 4.75f,
                RenderingSystem.getScreenSizeInMeters().y * 2.35f,
                3f, path, 50);
        placeQuestion(5.4f, 1.425f, 2, "F");
        createStar(
                7.5f * RenderingSystem.getScreenSizeInMeters().x,
                2f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );
    }

    private void createSection5() {
        createStar(
                6.5f * RenderingSystem.getScreenSizeInMeters().x,
                2.75f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );
        createStar(
                5.5f * RenderingSystem.getScreenSizeInMeters().x,
                2.75f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );
        createStar(
                4.5f * RenderingSystem.getScreenSizeInMeters().x,
                2.75f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );
        createStar(
                3.5f * RenderingSystem.getScreenSizeInMeters().x,
                2.75f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );
        final Entity dynamicWall = placeDynamicImpenetrableWall(1f, 2f, 0.015f, 0.4f);
        final Entity gate = placeDynamicImpenetrableWall(0.55f, 1.875f, 0.01f, 0.25f);
        placeImpenetrableWall(1f, 1.625f, 0.05f, 0.25f);
        placeImpenetrableWall(1f, 1.75f, 1f, 0.015f);
        final Entity barrierForKey = placeDynamicImpenetrableWall(0.35f, 1.72f, 0.5f, 0.01f);
        placeDynamicImpenetrableWall(2.5f, 2.75f, 0.5f, 0.395f);
        placeTransparentWall(1.25f, 2.4f, 0.5f, 0.01f);
        placeTransparentWall(1.5f, 1.75f, 0.05f, 0.5f);
        createStar(
                1.15f * RenderingSystem.getScreenSizeInMeters().x,
                1.625f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );
        placeImpenetrableWall(1.5f, 2.2f, 0.05f, 0.5f);
        placeImpenetrableWall(0.25f, 2f, 0.55f, 0.05f);
        placeImpenetrableWall(0.5f, 2.125f, 0.05f, 0.25f);
        placeMirror(0.5f, 2.4f, 1f, 0.05f, 0);
        placeMirror(0.12f, 1.9f, 0.4f, 0.01f, 70f);
        final Entity rotateTumbler = createTumbler(new Vector2(
                        1.4f * RenderingSystem.getScreenSizeInMeters().x,
                        1.55f * RenderingSystem.getScreenSizeInMeters().y),
                0.07f * RenderingSystem.getScreenSizeInMeters().x,
                0.04f * RenderingSystem.getScreenSizeInMeters().y, () -> {});
        Runnable rotateTask = new Runnable() {
            private int state = 1;

            @Override
            public void run() {
                if (state == 0) {
                    addAngularVelocity(dynamicWall, 0f);
                    gate.getComponent(BodyComponent.class).body.setLinearVelocity(0f, -100f);
                    rotateTumbler.getComponent(TextureComponent.class)
                            .region.setTexture(getManager()
                            .getImage(KittensAssetManager.Images.BLUE_TUMBLER));
                } else {
                    addAngularVelocity(dynamicWall, 2f);
                    gate.getComponent(BodyComponent.class).body.setLinearVelocity(0f, +100f);
                    rotateTumbler.getComponent(TextureComponent.class)
                            .region.setTexture(getManager()
                            .getImage(KittensAssetManager.Images.YELLOW_TUMBLER));
                }
                state ^= 1;
            }
        };
        rotateTumbler.getComponent(TumblerComponent.class).setAction(rotateTask);
        final Entity moveTumbler = createTumbler(new Vector2(
                        0.25f * RenderingSystem.getScreenSizeInMeters().x,
                        2.1f * RenderingSystem.getScreenSizeInMeters().y),
                0.07f * RenderingSystem.getScreenSizeInMeters().x,
                0.04f * RenderingSystem.getScreenSizeInMeters().y, () -> {});
        Runnable moveTask = () -> {
            barrierForKey.getComponent(BodyComponent.class).body.setLinearVelocity(+50f, 0);
            moveTumbler.getComponent(TextureComponent.class)
                    .region.setTexture(getManager().getImage(KittensAssetManager.Images.BLUE_TUMBLER));
        };
        moveTumbler.getComponent(TumblerComponent.class).setAction(moveTask);
        createStar(
                0.14f * RenderingSystem.getScreenSizeInMeters().x,
                1.55f * RenderingSystem.getScreenSizeInMeters().y,
                1f
        );
        placePointer(1.75f, 2f, 180);
        placePointer(7.5f, 2.5f, 0);
        placePointer(2.5f, 2.75f, 90);
    }

    private void createSection6() {
        placePointer(7.75f, 1f, 180);
        placeImpenetrableWall(7.25f, 1.5f, 0.5f, 0.05f);
        placeImpenetrableWall(7.5f, 0.9f, 0.05f, 1.2f);
        placePointer(7.25f, 0.5f, 0);
        placeImpenetrableWall(7f, 0.6f, 0.05f, 1.2f);
        placeImpenetrableWall(6.5f, 1.25f, 0.05f, 0.5f);
        placeImpenetrableWall(6.5f, 0.4f, 0.05f, 0.7f);
        placeTransparentWall(6.75f, 0.3f, 0.5f, 0.05f);
        placeTransparentWall(6.4f, 1.25f, 0.05f, 0.5f);
        final Entity barrier = placeDynamicImpenetrableWall(6.45f, 0.85f, 0.01f, 0.4f);
        final Entity moveTumbler = createTumbler(new Vector2(
                        6.75f * RenderingSystem.getScreenSizeInMeters().x,
                        0.1f * RenderingSystem.getScreenSizeInMeters().y),
                0.07f * RenderingSystem.getScreenSizeInMeters().x,
                0.04f * RenderingSystem.getScreenSizeInMeters().y, () -> {});
        final Entity door = placeTransparentWall(6.5f, 0.875f, 0.01f, 0.25f);
        final Entity gate1 = placeDynamicImpenetrableWall(5.75f, 0.575f, 0.5f, 0.025f);
        final Entity gate2 = placeDynamicImpenetrableWall(5.25f, 0.475f, 0.5f, 0.025f);
        Runnable moveTask = new Runnable() {
            private int state = 0;
            @Override
            public void run() {
                if (state == 0) {
                    barrier.getComponent(BodyComponent.class).body.setLinearVelocity(0, +500f);
                    state = 1;
                } else if (state == 1) {
                    if (barrier.getComponent(BodyComponent.class).body.getPosition().x >
                            1.24f * RenderingSystem.getScreenSizeInMeters().x) {
                        door.getComponent(StateComponent.class).finish();
                        moveTumbler.getComponent(TextureComponent.class)
                                .region.setTexture(getManager()
                                .getImage(KittensAssetManager.Images.BLUE_TUMBLER));
//                        gate.getComponent(BodyComponent.class).body.setLinearVelocity(-40f, 0);
                        state = 2;
                    }
                } else if (state == 2) {
                    gate1.getComponent(BodyComponent.class).body.setLinearVelocity(+15f, 0);
                    gate2.getComponent(BodyComponent.class).body.setLinearVelocity(-15f, 0);
                    state = 3;
                } else {
                    gate1.getComponent(BodyComponent.class).body.setLinearVelocity(-15f, 0);
                    gate2.getComponent(BodyComponent.class).body.setLinearVelocity(+15f, 0);
                    state = 2;
                }
            }
        };
        moveTumbler.getComponent(TumblerComponent.class).setAction(moveTask);
        placePointer(6.75f, 0.5f, 180);
        placePointer(6.5f, 0.9f, 90);
        Entity upDoor = placeDoor(6.22f, 1.25f, 0.4f, 0.05f);
        placeImpenetrableWall(6f, 0.875f, 0.05f, 0.9f);
        placeImpenetrableWall(5.75f, 0.75f, 0.5f, 0.05f);

        placeTransparentWall(5.5f, 0.6f, 0.5f, 0.05f);
        placeTransparentWall(5.5f, 0.5f, 0.5f, 0.05f);
        placeTransparentWall(5.5f, 0.4f, 0.5f, 0.05f);

        createStar(
                5.75f * RenderingSystem.getScreenSizeInMeters().x,
                1f * RenderingSystem.getScreenSizeInMeters().y,
                3f
        );

        createKey(new Vector2(
                        5.5f * RenderingSystem.getScreenSizeInMeters().x,
                        0.65f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                upDoor);
    }

    @Override
    public void createLevel(int widthInScreens, int heightInScreens, AbstractLevel abstractLevel) {
        CW = widthInScreens;
        CH = heightInScreens;

        createBackground(widthInScreens, heightInScreens);

        abstractLevel.setPlayer(
                createPlayer(
                RenderingSystem.getScreenSizeInMeters().x * 3.5f,
                RenderingSystem.getScreenSizeInMeters().y * 0.5f,
                3f));

        createStar(
                7.5f * RenderingSystem.getScreenSizeInMeters().x,
                2f * RenderingSystem.getScreenSizeInMeters().y,
                2f
        );

        createBorders();
        createSection1();
        createSection2();
        createSection3();
        createSection4();

        Entity doorBetweenSections = placeDoor(7.5f, 1.6f, 1f, 0.05f);
        createKey(new Vector2(
                        0.5f * RenderingSystem.getScreenSizeInMeters().x,
                        1.6f * RenderingSystem.getScreenSizeInMeters().y),
                0.05f * RenderingSystem.getScreenSizeInMeters().x,
                0.03f * RenderingSystem.getScreenSizeInMeters().y,
                doorBetweenSections);

        createSection5();
        createSection6();
    }
}
