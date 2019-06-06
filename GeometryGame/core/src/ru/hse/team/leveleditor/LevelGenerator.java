package ru.hse.team.leveleditor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.database.levels.SavedLevel;
import ru.hse.team.database.levels.SimpleEntity;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

/**
 * Class for generating levels from {@code SavedLevel} instances.
 */
public class LevelGenerator {

    /**
     * Creates new level with characteristics from given SavedLevel instance.
     * Creates all entities from it.
     * Only one of player instances will be created and
     *  if there is no player, new one will be created
     */
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

    private static AbstractLevelFactory createFactory(SavedLevel savedLevel, PooledEngine engine,
                                                      KittensAssetManager kittensAssetManager, BodyFactory bodyFactory) {
        class LevelFactory extends AbstractLevelFactory {

            public LevelFactory(PooledEngine engine, KittensAssetManager manager, BodyFactory bodyFactory) {
                super(engine, manager, bodyFactory);
            }

            @Override
            public void createLevel(int widthInScreens, int heightInScreens, AbstractLevel abstractLevel) {
                createBackground(widthInScreens, heightInScreens);

                Entity focusedPlayer = null;

                for (SimpleEntity entity : savedLevel.entities) {

                    Vector2 scale = getCommonScale(entity);
                    switch (entity.getType()) {
                        case STAR:
                            createStar(entity.getPositionX() * RenderingSystem.WIDTH_TO_METERS,
                                    entity.getPositionY() * RenderingSystem.HEIGHT_TO_METERS,
                                    entity.getSizeX() * scale.x);
                            break;
                        case MIRROR:
                            createMirror(new Vector2(entity.getPositionX() * RenderingSystem.WIDTH_TO_METERS,
                                            entity.getPositionY() * RenderingSystem.HEIGHT_TO_METERS),
                                    entity.getSizeX() * scale.x, entity.getSizeY() * scale.y,
                                    entity.getRotation() * (float) Math.PI / 180);
                            break;
                        case WALL:
                            createImpenetrableWall(new Vector2(entity.getPositionX() * RenderingSystem.WIDTH_TO_METERS,
                                            entity.getPositionY() * RenderingSystem.HEIGHT_TO_METERS),
                                    entity.getSizeX() * scale.x, entity.getSizeY() * scale.y,
                                    entity.getRotation() * (float) Math.PI / 180);
                            break;
                        case GLASS:
                            createTransparentWall(new Vector2(entity.getPositionX() * RenderingSystem.WIDTH_TO_METERS,
                                            entity.getPositionY() * RenderingSystem.HEIGHT_TO_METERS),
                                    entity.getSizeX() * scale.x, entity.getSizeY() * scale.y,
                                    entity.getRotation() * (float) Math.PI / 180);
                            break;
                        case PLAYER:
                            if (focusedPlayer == null) {
                                focusedPlayer = createPlayer(entity.getPositionX() * RenderingSystem.WIDTH_TO_METERS,
                                        entity.getPositionY() * RenderingSystem.HEIGHT_TO_METERS,
                                        entity.getSizeX() * scale.x);
                            }
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


    /**
     * Makes entities in level look exactly like in editor.
     */
    private static Vector2 getCommonScale(SimpleEntity entity) {
        Vector2 scale;
        switch (entity.getType()) {
            case STAR:
                scale = new Vector2(RenderingSystem.WIDTH_TO_METERS / 2,
                        RenderingSystem.HEIGHT_TO_METERS / 2);
                break;
            case PLAYER:
                scale = new Vector2(0.39f * RenderingSystem.WIDTH_TO_METERS,
                        0.39f * RenderingSystem.HEIGHT_TO_METERS);
                break;
            default:
                scale = new Vector2(1 * RenderingSystem.WIDTH_TO_METERS,
                        1 * RenderingSystem.HEIGHT_TO_METERS);
        }
        scale.x /= LaserKittens.scaleToPreferredWidth();
        scale.y /= LaserKittens.scaleToPreferredHeight();
        return scale;
    }

}
