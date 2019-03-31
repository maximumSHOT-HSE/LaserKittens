package com.example.learning.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.example.learning.game.gamelogic.components.PlayerComponent;

public class PlayerControlSystem extends IteratingSystem{

    @SuppressWarnings("unchecked")
    public PlayerControlSystem() {
        super(Family.all(PlayerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}