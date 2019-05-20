package ru.hse.team.game.levels.Quiz;

import com.badlogic.ashley.core.PooledEngine;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class QuizLevel extends AbstractLevel {

    private QuizLevelFactory quizLevelFactory = new QuizLevelFactory();

    public QuizLevel() {
        super("Quiz");
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        quizLevelFactory.setLevelSize(1, 1);
        quizLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return quizLevelFactory;
    }
}
