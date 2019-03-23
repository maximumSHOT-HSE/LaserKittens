package com.example.learning;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.example.learning.gamelogic.components.BodyComponent;
import com.example.learning.gamelogic.components.CollisionComponent;
import com.example.learning.gamelogic.components.PlayerComponent;
import com.example.learning.gamelogic.components.StateComponent;
import com.example.learning.gamelogic.components.TextureComponent;
import com.example.learning.gamelogic.components.TransformComponent;
import com.example.learning.gamelogic.components.TypeComponent;
import com.example.learning.gamelogic.systems.AnimationSystem;
import com.example.learning.gamelogic.systems.CollisionSystem;
import com.example.learning.gamelogic.systems.PhysicsDebugSystem;
import com.example.learning.gamelogic.systems.PhysicsSystem;
import com.example.learning.gamelogic.systems.PlayerControlSystem;
import com.example.learning.gamelogic.systems.RenderingSystem;

public class GameScreen implements Screen {

    private final LaserKittens parent;
    private OrthographicCamera camera;
    private PooledEngine engine;
    private LevelFactory levelFactory;


    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
    private Background background;
    private GestureDetector controller;

    public GameScreen(LaserKittens geometryGame) {
        this.parent = geometryGame;
        background = new Background(parent.assetManager.manager.get("blue-background.jpg", Texture.class));

        engine = new PooledEngine();
        levelFactory = new LevelFactory(engine, parent.assetManager);

        // Create our new rendering system
        RenderingSystem renderingSystem = new RenderingSystem(parent.batch);
        camera = renderingSystem.getCamera();
        controller = new GestureDetector(new ModelGestureListener(camera));

        parent.batch.setProjectionMatrix(camera.combined);

        engine.addSystem(new AnimationSystem());
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(levelFactory.world));
        engine.addSystem(new PhysicsDebugSystem(levelFactory.world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem());

        levelFactory.createPlayer();
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        //debugRenderer.render(world, camera.combined);
        engine.update(delta);

        parent.batch.begin();
        background.draw(parent.batch, camera);//TODO draw it with rendering system
        parent.batch.end();
        debugRenderer.render(levelFactory.world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {

    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
