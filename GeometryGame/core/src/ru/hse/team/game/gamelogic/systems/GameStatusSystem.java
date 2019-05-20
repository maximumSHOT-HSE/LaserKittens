package ru.hse.team.game.gamelogic.systems;

import com.badlogic.ashley.core.EntitySystem;
import ru.hse.team.game.gamelogic.GameStatus;

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
