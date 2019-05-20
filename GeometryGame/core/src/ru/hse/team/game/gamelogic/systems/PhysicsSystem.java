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
import ru.hse.team.game.gamelogic.components.TransformComponent;


/**
 *  Do world step iterations.
 *  Once in MAX_STEP_TIME
 *  Controls Transform position be equal to body position
 *  for entities with bodies.
 */
public class PhysicsSystem extends IteratingSystem {

    private static final float MAX_STEP_TIME = 1 / 60f;
    private static float accumulator = 0f;

    private World world;
    private Array<Entity> bodiesQueue;

    @SuppressWarnings("unchecked")
    public PhysicsSystem(World world) {
        super(Family.all(
                BodyComponent.class,
                TransformComponent.class
        ).get());
        this.world = world;
        this.bodiesQueue = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        if(accumulator >= MAX_STEP_TIME) {
            world.step(MAX_STEP_TIME, 6, 2);
            accumulator -= MAX_STEP_TIME;
            for (Entity entity : bodiesQueue) {
                TransformComponent transformComponent = Mapper.transformComponent.get(entity);
                BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
                if (bodyComponent == null || bodyComponent.body == null) continue;
                Vector2 position = bodyComponent.body.getPosition();
                transformComponent.position.x = position.x;
                transformComponent.position.y = position.y;
                transformComponent.rotation = bodyComponent.body.getAngle() * MathUtils.radiansToDegrees;
            }
            bodiesQueue.clear();
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        bodiesQueue.add(entity);
    }
}
