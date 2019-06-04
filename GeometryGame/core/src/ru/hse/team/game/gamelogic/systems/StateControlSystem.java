package ru.hse.team.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;

import java.util.concurrent.TimeUnit;

import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.DoorComponent;
import ru.hse.team.game.gamelogic.components.StateComponent;
import ru.hse.team.game.gamelogic.components.TypeComponent;
import ru.hse.team.game.levels.AbstractLevel;

/**
 * Processes objects state.
 * Destroys objects in FINISHED state
 * NORMAL state is neutral
 * Processes JUST_CREATED objects according to their type and changes
 *  their state to NORMAL
 */
public class StateControlSystem extends IteratingSystem {

    private PooledEngine engine;
    private AbstractLevel abstractLevel;

    public StateControlSystem(PooledEngine engine, AbstractLevel abstractLevel) {
        super(Family.all(StateComponent.class).get());
        this.engine = engine;
        this.abstractLevel = abstractLevel;
    }

    @Override
    public void update(float delta) {
        for (Entity entity : getEntities()) {
            processEntity(entity, delta);
        }
        abstractLevel.getGameStatus().update(delta);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent stateComponent = Mapper.stateComponent.get(entity);
        TypeComponent typeComponent = Mapper.typeComponent.get(entity); // may be null

        switch (stateComponent.get()) {
            case JUST_CREATED:
                stateComponent.set(StateComponent.State.NORMAL);
                if (typeComponent != null && typeComponent.type == TypeComponent.Type.STAR) {
                    System.out.println("ADD STAR");
                    abstractLevel.getGameStatus().addStar();
                }
                break;
            case FINISHED:
                removeEntity(entity);
                break;
        }

    }

    private void removeEntity(Entity entity) {
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity); // may be null
            TypeComponent typeComponent = Mapper.typeComponent.get(entity); // may be null

            if (typeComponent != null) {
                switch (typeComponent.type) {
                    case STAR:
                        System.out.println("REMOVE STAR!");
                        abstractLevel.getGameStatus().removeStar();
                        break;
                    case KEY:
                        Entity door = Mapper.keyComponent.get(entity).door;
                        StateComponent doorState = Mapper.stateComponent.get(door);
                        DoorComponent doorComponent = Mapper.doorComponent.get(door);
                        doorComponent.removeKey(entity);
                        if (doorComponent.remainingKeys() == 0) {
                            if (abstractLevel != null && abstractLevel.getAbstractGraph() != null &&
                                    bodyComponent != null && bodyComponent.body != null) {
                                abstractLevel.getAbstractGraph().updateGraphAfterRemoveRectangleBarrier(doorState.getId());
                            }
                            doorState.finish();
                        }
                        break;
                    case GUARDIAN:
                        abstractLevel.getGameStatus().addPenaltyNanoTime(TimeUnit.MINUTES.toNanos(1));
                        break;
                }
            }

            if (bodyComponent != null && bodyComponent.body != null && abstractLevel.getWorld() != null) {
                //null checking is the best way to avoid sigfault related with libgdx!!!
                abstractLevel.getWorld().destroyBody(bodyComponent.body);
                bodyComponent.body = null;
            }
            engine.removeEntity(entity);
    }
}
