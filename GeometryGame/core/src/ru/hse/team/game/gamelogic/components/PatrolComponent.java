package ru.hse.team.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PatrolComponent implements Component, Pool.Poolable {

    private static final float EPS = (float) 1;

    /*
    * 0 -> 1 -> 2 -> ... -> (n - 1) -> 0 -> 1 ...
    * */
    private List<Vector2> path = new ArrayList<>();
    private int currentVertex;
    private Entity character;
    private float velocity;

    public void setPath(List<Vector2> path) {
        this.path = path;
        currentVertex = 0;
    }

    public void setEntity(Entity character) {
        this.character = character;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void action() {
        int nextVertex = (currentVertex + 1) % path.size();
        Vector2 currentPosition = character.getComponent(BodyComponent.class).body.getPosition();
        if (currentPosition.cpy().sub(path.get(nextVertex)).len() < EPS) {
            currentVertex = nextVertex;
            nextVertex = (nextVertex + 1) % path.size();
        }
        Vector2 v = path.get(nextVertex).cpy().sub(currentPosition);
        v.scl(velocity / v.len());
        character.getComponent(BodyComponent.class).body.setLinearVelocity(v);
    }

    @Override
    public void reset() {

    }
}
