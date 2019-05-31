package ru.hse.team.game.Multiplayer;

import ru.hse.team.game.levels.AbstractLevel;

abstract public class AbstractMultiplayerLevel extends AbstractLevel {

    public AbstractMultiplayerLevel(String levelName, int widthInScreens, int heightInScreens) {
        super(levelName, widthInScreens, heightInScreens);
    }
}
