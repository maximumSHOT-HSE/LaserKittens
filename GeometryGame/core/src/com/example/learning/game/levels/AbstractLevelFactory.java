package com.example.learning.game.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.World;

abstract public class AbstractLevelFactory {

    abstract public World getWorld();

    abstract public Entity getPlayer();
}
