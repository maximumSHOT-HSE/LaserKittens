package com.example.learning.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.example.learning.game.GameScreen;
import com.example.learning.game.Mapper;
import com.example.learning.game.gamelogic.components.PlayerComponent;

public class GameStatusSystem extends IteratingSystem {

    private GameScreen gameScreen;

    public GameStatusSystem(GameScreen gameScreen) {
        super(Family.all(
            PlayerComponent.class
        ).get());
        this.gameScreen = gameScreen;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerComponent = Mapper.playerComponent.get(entity);
        if (playerComponent.readyToFinish()) {
            gameScreen.endGame();
        }
    }
}
