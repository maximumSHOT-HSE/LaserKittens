package ru.hse.team.game.gamelogic.systems;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.PatrolComponent;
import ru.hse.team.game.gamelogic.components.TransformComponent;
import ru.hse.team.game.gamelogic.components.TypeComponent;
import ru.hse.team.game.levels.AbstractLevel;


/**
 *  Do world step iterations.
 *  Once in MAX_STEP_TIME
 *  Controls Transform position be equal to body position
 *  for entities with bodies.
 */
public class PhysicsSystem extends IteratingSystem {

    private static final float MAX_STEP_TIME = 1 / 60f;
    private static float accumulator = 0f;

    private AbstractLevel abstractLevel;

    public PhysicsSystem(AbstractLevel abstractLevel) {
        super(Family.all(
                BodyComponent.class,
                TransformComponent.class
        ).get());
        this.abstractLevel = abstractLevel;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        if(accumulator >= MAX_STEP_TIME) {
            abstractLevel.getWorld().step(MAX_STEP_TIME, 6, 2);
            accumulator -= MAX_STEP_TIME;

            changePositions();
            patrolTerritory();
            updateGraph();
        }
    }

    private void changePositions() {
        for (Entity entity : getEntities()) {
            TransformComponent transformComponent = Mapper.transformComponent.get(entity);
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
            if (bodyComponent == null || bodyComponent.body == null) {
                continue;
            }
            Vector2 position = bodyComponent.body.getPosition();
            transformComponent.position.x = position.x;
            transformComponent.position.y = position.y;
            transformComponent.rotation = bodyComponent.body.getAngle() * MathUtils.radiansToDegrees;
        }
    }

    private void patrolTerritory() {
        for (Entity entity : getEntities()) {
            PatrolComponent patrolComponent = Mapper.patrolComponent.get(entity);
            if (patrolComponent == null) {
                continue;
            }
            patrolComponent.action();
        }
    }

    private void updateGraph() {
        if (abstractLevel.getAbstractGraph() != null) {
            for (Entity entity : getEntities()) {
                TypeComponent typeComponent = Mapper.typeComponent.get(entity);
                if (typeComponent == null) {
                    continue;
                }
                BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
                if (bodyComponent == null || bodyComponent.body == null) {
                    continue;
                }
                if (typeComponent.type.equals(TypeComponent.Type.PLAYER)) {
                    abstractLevel
                            .getAbstractGraph()
                            .visit(bodyComponent.body.getPosition());
                }
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }
}
