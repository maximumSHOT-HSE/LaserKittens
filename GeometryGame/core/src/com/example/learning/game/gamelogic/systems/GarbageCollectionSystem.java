package com.example.learning.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.example.learning.game.Mapper;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.GameStatus;
import com.example.learning.game.gamelogic.components.StateComponent;
import com.example.learning.game.gamelogic.components.TypeComponent;

public class GarbageCollectionSystem extends IteratingSystem {

    private World world;
    private PooledEngine engine;
    private GameStatus gameStatus;

    public GarbageCollectionSystem(World world, PooledEngine engine, GameStatus gameStatus) {
        super(
            Family.all(
                StateComponent.class
            ).get()
        );
        this.world = world;
        this.engine = engine;
        this.gameStatus = gameStatus;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent stateComponent = Mapper.stateComponent.get(entity);
        TypeComponent typeComponent = Mapper.typeComponent.get(entity);//may be null

        if (stateComponent.get() == StateComponent.State.FINISHED) {
            engine.removeEntity(entity);
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
            if (bodyComponent != null) {
                world.destroyBody(bodyComponent.body);
            }
            if (typeComponent != null && typeComponent.type == TypeComponent.ObjectType.STAR) {
                gameStatus.removeStar();
            }
        }
        if (stateComponent.get() == StateComponent.State.JUST_CREATED) {
            stateComponent.set(StateComponent.State.NORMAL);
            if (typeComponent != null && typeComponent.type == TypeComponent.ObjectType.STAR) {
                gameStatus.addStar();
            }
        }
    }
}
