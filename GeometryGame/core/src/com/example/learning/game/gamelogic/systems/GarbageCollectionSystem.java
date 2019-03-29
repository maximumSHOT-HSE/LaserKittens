package com.example.learning.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.game.Mapper;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.StateComponent;

public class GarbageCollectionSystem extends IteratingSystem {

    private World world;
    private PooledEngine engine;

    public GarbageCollectionSystem(World world, PooledEngine engine) {
        super(
            Family.all(
                StateComponent.class
            ).get()
        );
        this.world = world;
        this.engine = engine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent stateComponent = Mapper.stateComponent.get(entity);
        if (stateComponent.get() == StateComponent.State.FINISHED) {
            engine.removeEntity(entity);
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
            if (bodyComponent != null) {
                world.destroyBody(bodyComponent.body);
            }
        }
    }
}
