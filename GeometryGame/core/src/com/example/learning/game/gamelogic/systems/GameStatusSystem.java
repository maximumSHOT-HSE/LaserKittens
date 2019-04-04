package com.example.learning.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.example.learning.game.GameScreen;
import com.example.learning.game.Mapper;
import com.example.learning.game.gamelogic.GameStatus;
import com.example.learning.game.gamelogic.components.PlayerComponent;

/**
 * Control game status.
 * Ends level
 */
public class GameStatusSystem extends EntitySystem {

    private GameStatus gameStatus;

    public GameStatusSystem(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void update(float deltaTime) {
        gameStatus.addTime(deltaTime);
        if (gameStatus.readyToFinish()) {
            gameStatus.getGameScreen().endGame();
        }
    }
}
