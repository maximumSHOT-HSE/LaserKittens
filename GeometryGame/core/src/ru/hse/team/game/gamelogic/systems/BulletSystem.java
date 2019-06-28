package ru.hse.team.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.components.BulletComponent;
import ru.hse.team.game.gamelogic.components.StateComponent;

/**
 * System for bullets processing.
 *  Kills it if it is too long on a screen
 *  Prevents bullet path from being perceptibly long
 */
public class BulletSystem extends IteratingSystem {
    private static final int MAX_BULLET_PATH_SIZE = 20;

    public BulletSystem() {
        super(Family.all(BulletComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BulletComponent bulletComponent = Mapper.bulletComponent.get(entity);
        StateComponent stateComponent = Mapper.stateComponent.get(entity);

        while (bulletComponent.path.size() > MAX_BULLET_PATH_SIZE) {
            bulletComponent.path.remove(0);
        }

        if (System.currentTimeMillis() - bulletComponent.creationTime >= bulletComponent.lifeTime) {
            stateComponent.finish();
            bulletComponent.path.clear();
        }
    }
}
