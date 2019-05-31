package ru.hse.team.leveleditor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.database.levels.SavedLevel;
import ru.hse.team.database.levels.SimpleEntity;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class LevelGenerator {

    public static AbstractLevel generate(SavedLevel savedLevel) {

        return new AbstractLevel(savedLevel.levelName, savedLevel.widthInScreens, savedLevel.heightInScreens) {

            private AbstractLevelFactory factory;

            @Override
            public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
                factory = createFactory(savedLevel, engine, assetManager, getBodyFactory());
                factory.createLevel(getLevelWidthInScreens(), getLevelHeightInScreens(), this);
            }

            @Override
            public AbstractLevelFactory getFactory() {
                return factory;
            }
        };
    }

    private static AbstractLevelFactory createFactory(SavedLevel savedLevel, PooledEngine engine, KittensAssetManager kittensAssetManager, BodyFactory bodyFactory) {
        class LevelFactory extends AbstractLevelFactory {

            public LevelFactory(PooledEngine engine, KittensAssetManager manager, BodyFactory bodyFactory) {
                super(engine, manager, bodyFactory);
            }

            @Override
            public void createLevel(int widthInScreens, int heightInScreens, AbstractLevel abstractLevel) {
                createBackground(widthInScreens, heightInScreens);

                Entity focusedPlayer = null;

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
                                    entity.getSizeX() * scale.x, entity.getSizeY() * scale.y, entity.getRotation() * (float)Math.PI / 180);
                            break;
                        case GLASS:
                            createTransparentWall(new Vector2(entity.getPositionX() * PM, entity.getPositionY() * PM),
                                    entity.getSizeX() * scale.x, entity.getSizeY() * scale.y, entity.getRotation() * (float)Math.PI / 180);
                            break;
                        case PLAYER:
                            focusedPlayer = createPlayer(entity.getPositionX() * PM, entity.getPositionY() * PM, entity.getSizeX() * scale.x);
                            break;
                    }
                }

                if (focusedPlayer == null) {
                    focusedPlayer = createPlayer(10, 10, 3);
                }

                abstractLevel.setPlayer(focusedPlayer);
            }
        };
        return new LevelFactory(engine, kittensAssetManager, bodyFactory);
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
