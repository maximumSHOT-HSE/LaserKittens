package ru.hse.team.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Renders world with box2DDebugRenderer.
 */
public class PhysicsDebugSystem extends IteratingSystem {

    private boolean debug = false;

    private Box2DDebugRenderer debugRenderer;
    private World world;
    private OrthographicCamera camera;

    public PhysicsDebugSystem(World world, OrthographicCamera camera) {
        super(Family.all().get());
        debugRenderer =  new Box2DDebugRenderer(
                true, true, false, true, false, true);
        this.world = world;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (debug) {
            debugRenderer.render(world, camera.combined);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
