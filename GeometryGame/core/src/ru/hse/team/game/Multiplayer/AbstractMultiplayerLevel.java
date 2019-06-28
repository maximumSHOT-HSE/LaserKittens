package ru.hse.team.game.Multiplayer;

import ru.hse.team.game.levels.AbstractLevel;

abstract public class AbstractMultiplayerLevel extends AbstractLevel {
    private int role = -1;

    public AbstractMultiplayerLevel(String levelName, int widthInScreens, int heightInScreens) {
        super(levelName, widthInScreens, heightInScreens);
    }

    abstract public int getNumberOfPlayers();

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
