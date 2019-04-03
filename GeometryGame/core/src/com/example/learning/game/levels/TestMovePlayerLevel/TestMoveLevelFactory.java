package com.example.learning.game.levels.TestMovePlayerLevel;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.example.learning.MyAssetManager;
import com.example.learning.game.BodyFactory;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.TextureComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;
import com.example.learning.game.gamelogic.systems.RenderingSystem;
import com.example.learning.game.levels.AbstractLevelFactory;

public class TestMoveLevelFactory extends AbstractLevelFactory {

    public TestMoveLevelFactory(){
        world = new World(new Vector2(0,0), false);
    }

    @Override
    public World getWorld() {
        return world;
    }


    @Override
    public void createLevel(PooledEngine engine, MyAssetManager assetManager) {
        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();
        focusedPlayer = createPlayer(5, 5, 5);
    }
}
