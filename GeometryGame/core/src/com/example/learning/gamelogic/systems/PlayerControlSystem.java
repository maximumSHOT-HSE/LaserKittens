package com.example.learning.gamelogic.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.example.learning.gamelogic.components.BodyComponent;
import com.example.learning.gamelogic.components.PlayerComponent;
import com.example.learning.gamelogic.components.StateComponent;

public class PlayerControlSystem extends IteratingSystem{

    ComponentMapper<PlayerComponent> pm;
    ComponentMapper<BodyComponent> bodm;
    ComponentMapper<StateComponent> sm;


    @SuppressWarnings("unchecked")
    public PlayerControlSystem() {
        super(Family.all(PlayerComponent.class).get());

        pm = ComponentMapper.getFor(PlayerComponent.class);
        bodm = ComponentMapper.getFor(BodyComponent.class);
        sm = ComponentMapper.getFor(StateComponent.class);

    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BodyComponent b2body = bodm.get(entity);
        StateComponent state = sm.get(entity);


    }
}