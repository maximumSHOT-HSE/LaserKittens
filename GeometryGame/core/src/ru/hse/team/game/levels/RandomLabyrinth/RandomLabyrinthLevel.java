package ru.hse.team.game.levels.RandomLabyrinth;

import com.badlogic.ashley.core.PooledEngine;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.gamelogic.algorithms.GridGraph;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class RandomLabyrinthLevel extends AbstractLevel {

    private RandomLabyrinthLevelFactory randomLabyrinthLevelFactory;

    private int keys;
    private int stars;

    public RandomLabyrinthLevel(int widthInScreens, int heightInScreens, int keys, int stars) {
        super("Random Labyrinth", widthInScreens, heightInScreens);
        if (stars <= 0) {
            throw new IllegalArgumentException("There should be at least one star");
        }
        if (widthInScreens * heightInScreens * 2 < keys + stars + 1) {
            throw new IllegalArgumentException("No enough place for keys and stars");
        }
        this.keys = keys;
        this.stars = stars;
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        randomLabyrinthLevelFactory = new RandomLabyrinthLevelFactory(engine, assetManager, getBodyFactory());
        randomLabyrinthLevelFactory.setKeys(keys);
        randomLabyrinthLevelFactory.setStars(stars);
        randomLabyrinthLevelFactory.createLevel(getLevelWidthInScreens(), getLevelHeightInScreens(), this);

        setAbstractGraph(new GridGraph(getLevelWidthInScreens(), getLevelHeightInScreens(),
                RenderingSystem.getScreenSizeInMeters().x,
                RenderingSystem.getScreenSizeInMeters().y));
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return randomLabyrinthLevelFactory;
    }
}
