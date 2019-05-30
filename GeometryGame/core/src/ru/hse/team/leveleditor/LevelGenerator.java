package ru.hse.team.leveleditor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.database.levels.SavedLevel;
import ru.hse.team.database.levels.SimpleEntity;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class LevelGenerator {

    public static AbstractLevel generate(SavedLevel savedLevel) {
        Calendar nowCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = (SimpleDateFormat)SimpleDateFormat.getTimeInstance();
        dateFormat.applyPattern("yyyy/MM/dd HH:mm");
        String levelName = "Generated " + dateFormat.format(nowCalendar.getTime());

        return new AbstractLevel(levelName) {

            private AbstractLevelFactory factory;

            @Override
            public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
                factory = createFactory(savedLevel);
                factory.setLevelSize(savedLevel.widthInScreens, savedLevel.heightInScreens);
                factory.createLevel(engine, assetManager);
            }

            @Override
            public AbstractLevelFactory getFactory() {
                return factory;
            }
        };
    }

    private static AbstractLevelFactory createFactory(SavedLevel savedLevel) {
        class LevelFactory extends AbstractLevelFactory {

            public LevelFactory() {
                world = new World(new Vector2(0,0), true);
            }

            @Override
            public World getWorld() {
                return world;
            }

            @Override
            public Entity getPlayer() {
                return focusedPlayer;
            }

            @Override
            public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
                this.engine = engine;
                this.manager = assetManager;
                bodyFactory = BodyFactory.getBodyFactory(world);
                createBackground();

                for (SimpleEntity entity : savedLevel.entities) {

                    float PM = RenderingSystem.PIXELS_TO_METRES;
                    Vector2 scale = getCommonScale(entity);
                    switch (entity.getType()) {
                        case STAR:
                            createStar(entity.getPositionX() * PM, entity.getPositionY() * PM, entity.getSizeX() * scale.x);
                            break;
                        case MIRROR:
                            createMirror(new Vector2(entity.getPositionX() * PM, entity.getPositionY() * PM),
                                    entity.getSizeX() * scale.x, entity.getSizeY() * scale.y, entity.getRotation() * (float)Math.PI / 180);
                            break;
                        case WALL:
                            createImpenetrableWall(new Vector2(entity.getPositionX() * PM, entity.getPositionY() * PM),
                                    entity.getSizeX() * scale.x, entity.getSizeY() * scale.y);
                            break;
                        case PLAYER:
                            focusedPlayer = createPlayer(entity.getPositionX() * PM, entity.getPositionY() * PM, entity.getSizeX() * scale.x);
                            break;
                    }
                }

                if (focusedPlayer == null) {
                    focusedPlayer = createPlayer(10, 10, 3);
                }
            }
        };
        return new LevelFactory();
    }



    private static Vector2 getCommonScale(SimpleEntity entity) {
        float PM = RenderingSystem.PIXELS_TO_METRES;
        switch (entity.getType()) {
            case STAR:
                return new Vector2(PM / 2, PM / 2);
            case PLAYER:
                return new Vector2(0.39f * PM, 0.39f * PM);
            default:
                return new Vector2(1 * PM, 1 * PM);
        }
    }

}
