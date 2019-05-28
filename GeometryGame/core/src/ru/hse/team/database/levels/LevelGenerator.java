package ru.hse.team.database.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.GameStatus;
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

            private Entity player;

            @Override
            public World getWorld() {
                return world;
            }

            @Override
            public Entity getPlayer() {
                return player;
            }

            @Override
            public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
                this.engine = engine;
                this.manager = assetManager;
                bodyFactory = BodyFactory.getBodyFactory(world);
                createBackground();

                for (SavedSimpleEntity entity : savedLevel.entities) {
                    switch (entity.getType()) {
                        case STAR:
                            createStar(entity.getPositionX(), entity.getPositionY(), entity.getSizeX());
                            break;
                        case MIRROR:
                            createMirror(new Vector2(entity.getPositionX(), entity.getPositionY()), entity.getSizeX(), entity.getSizeY());
                            break;
                        case WALL:
                            createImpenetrableWall(new Vector2(entity.getPositionX(), entity.getPositionY()), entity.getSizeX(), entity.getSizeY());
                            break;
                        case PLAYER:
                            player = createPlayer(entity.getPositionX(), entity.getPositionY(), entity.getSizeX());
                            break;
                    }
                }
            }
        };
        return new LevelFactory();
    }
}
