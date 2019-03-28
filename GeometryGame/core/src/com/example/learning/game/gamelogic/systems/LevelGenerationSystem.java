package com.example.learning.game.gamelogic.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.example.learning.game.TestMoveLevelFactory;
import com.example.learning.game.gamelogic.components.PlayerComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;

public class LevelGenerationSystem extends IteratingSystem {

    // get transform component so we can check players height
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private TestMoveLevelFactory testMoveLevelFactory;

    public LevelGenerationSystem(TestMoveLevelFactory lvlFactory){
        super(Family.all(PlayerComponent.class).get());
        testMoveLevelFactory = lvlFactory;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}