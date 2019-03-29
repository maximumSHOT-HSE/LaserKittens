package com.example.learning.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.example.learning.game.Mapper;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.BulletComponent;
import com.example.learning.game.gamelogic.components.StateComponent;

public class BulletSystem extends IteratingSystem {

    public BulletSystem() {
        super(Family.all(BulletComponent.class, StateComponent.class, BodyComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BulletComponent bulletComponent = Mapper.bulletComponent.get(entity);
        StateComponent stateComponent = Mapper.stateComponent.get(entity);
        BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
        if (System.currentTimeMillis() - bulletComponent.creationTime >= bulletComponent.lifeTime) {
            stateComponent.finish();
            bulletComponent.path.clear();
        } else {
            Vector2 current = bodyComponent.body.getPosition();
//            if (!bulletComponent.path.get(bulletComponent.path.size() - 1).equals(current)) {
                bulletComponent.path.add(current);
//            }
        }
    }
}
