package com.example.learning.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.example.learning.game.gamelogic.components.PlayerComponent;
import com.example.learning.game.levels.AbstractLevelFactory;

import java.util.Random;

public class LevelGenerationSystem extends IteratingSystem {

    // get transform component so we can check players height
    private AbstractLevelFactory abstractLevelFactory;
    private float timeAccumulator;
    private static final float DELTA_MODIFICATION_TIME = 1;
    private static final Random random = new Random(179);

    public LevelGenerationSystem(AbstractLevelFactory abstractLevelFactory) {
        super(Family.all(PlayerComponent.class).get());
        this.abstractLevelFactory = abstractLevelFactory;
    }

    @Override
    public void update(float deltaTime) {
        timeAccumulator += deltaTime;
        if (timeAccumulator > DELTA_MODIFICATION_TIME) {
            timeAccumulator = 0;
            float x = random.nextInt(1_000_000_000) / 1e9f;
            float y = random.nextInt(1_000_000_000) / 1e9f;
            x *= 0.5f * RenderingSystem.getScreenSizeInMeters().x;
            y *= 0.5f * RenderingSystem.getScreenSizeInMeters().y;
            abstractLevelFactory.createStar(
                x, y, 0.05f * RenderingSystem.getScreenSizeInMeters().y
            );
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}