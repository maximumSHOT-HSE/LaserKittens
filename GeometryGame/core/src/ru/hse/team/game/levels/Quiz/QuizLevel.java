package ru.hse.team.game.levels.Quiz;

import com.badlogic.ashley.core.PooledEngine;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.gamelogic.algorithms.GridGraph;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class QuizLevel extends AbstractLevel {

    private static final int WIDTH_SCREENS = 8;
    private static final int HEIGHT_SCREENS = 3;

    private QuizLevelFactory quizLevelFactory;

    public QuizLevel() {
        super("Quiz", WIDTH_SCREENS, HEIGHT_SCREENS);
        setAbstractGraph(new GridGraph(WIDTH_SCREENS, HEIGHT_SCREENS,
                RenderingSystem.getScreenSizeInMeters().x,
                RenderingSystem.getScreenSizeInMeters().y));
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        quizLevelFactory = new QuizLevelFactory(engine, assetManager, getBodyFactory());
        quizLevelFactory.createLevel(getLevelWidthInScreens(), getLevelHeightInScreens(), this);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return quizLevelFactory;
    }
}
