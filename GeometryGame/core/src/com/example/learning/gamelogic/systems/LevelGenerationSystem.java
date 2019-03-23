package com.example.learning.gamelogic.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.example.learning.LevelFactory;
import com.example.learning.gamelogic.components.PlayerComponent;
import com.example.learning.gamelogic.components.TransformComponent;

public class LevelGenerationSystem extends IteratingSystem {

    // get transform component so we can check players height
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private LevelFactory levelFactory;

    public LevelGenerationSystem(LevelFactory lvlFactory){
        super(Family.all(PlayerComponent.class).get());
        levelFactory = lvlFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}