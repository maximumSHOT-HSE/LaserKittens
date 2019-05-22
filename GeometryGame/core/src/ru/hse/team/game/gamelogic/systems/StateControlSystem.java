package ru.hse.team.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Map;

import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.GameStatus;
import ru.hse.team.game.gamelogic.components.DoorComponent;
import ru.hse.team.game.gamelogic.components.StateComponent;
import ru.hse.team.game.gamelogic.components.TypeComponent;

/**
 * Processes objects state.
 * Destroys objects in FINISHED state
 * NORMAL state is neutral
 * Processes other states specifically
 */
public class StateControlSystem extends IteratingSystem {

    private World world;
    private PooledEngine engine;
    private GameStatus gameStatus;

    public StateControlSystem(World world, PooledEngine engine, GameStatus gameStatus) {
        super(Family.all(StateComponent.class).get());
        this.world = world;
        this.engine = engine;
        this.gameStatus = gameStatus;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        StateComponent stateComponent = Mapper.stateComponent.get(entity);
        TypeComponent typeComponent = Mapper.typeComponent.get(entity); // may be null

        if (stateComponent.get() == StateComponent.State.FINISHED) {
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);

            if (typeComponent != null) {
                if (typeComponent.type == TypeComponent.Type.STAR) {
                    gameStatus.removeStar();
                }
                if (typeComponent.type == TypeComponent.Type.KEY) {
                    Entity door = Mapper.keyComponent.get(entity).door;
                    StateComponent doorState = Mapper.stateComponent.get(door);
                    DoorComponent doorComponent = Mapper.doorComponent.get(door);
                    doorComponent.removeKey(entity);
                    if (doorComponent.remainingKeys() == 0) {
                        doorState.finish();
                    }
                }
            }
            if (bodyComponent != null) {
                world.destroyBody(bodyComponent.body);
            }
            engine.removeEntity(entity);
        }
        if (stateComponent.get() == StateComponent.State.JUST_CREATED) {
            stateComponent.set(StateComponent.State.NORMAL);
            if (typeComponent != null && typeComponent.type == TypeComponent.Type.STAR) {
                gameStatus.addStar();
            }
        }
    }
}
