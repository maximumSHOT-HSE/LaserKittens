package com.example.learning.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.example.learning.game.Mapper;
import com.example.learning.game.gamelogic.components.BulletComponent;
import com.example.learning.game.gamelogic.components.StateComponent;

public class BulletSystem extends IteratingSystem {

    public BulletSystem() {
        super(Family.all(BulletComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BulletComponent bulletComponent = Mapper.bulletComponent.get(entity);
        StateComponent stateComponent = Mapper.stateComponent.get(entity);
        System.out.println("TIME !!!!!!!!!!! = " + (System.currentTimeMillis()));
        if (System.currentTimeMillis() - bulletComponent.creationTime >= bulletComponent.lifeTime) {
            stateComponent.finish();
        }
    }
}
