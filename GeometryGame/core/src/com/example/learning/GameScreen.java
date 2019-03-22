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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
    private OrthographicCamera camera = new OrthographicCamera(24, 32);
    private World world;
    private BodyFactory bodyFactory;
    private SpriteBatch sb;
    private PooledEngine engine;


    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
    private Background background = new Background("blue-background.jpg");
    private Stage stage;
    private GestureDetector controller = new GestureDetector(new ModelGestureListener(camera));

    public GameScreen(LaserKittens geometryGame) {
        this.parent = geometryGame;
        world = new World(new Vector2(0,-10f), true);
        bodyFactory = BodyFactory.getBodyFactory(world);

        sb = new SpriteBatch();
        // Create our new rendering system
        RenderingSystem renderingSystem = new RenderingSystem(sb);
        camera = renderingSystem.getCamera();

        sb.setProjectionMatrix(camera.combined);

        engine = new PooledEngine();
        engine.addSystem(new AnimationSystem());
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, renderingSystem.getCamera()));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem());

        createPlayer();
    }

    @Override
    public void show() {


        //Gdx.input.setInputProcessor(stage);
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render (float delta) {

        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {

    }


    private void createPlayer(){

        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        BodyComponent body = engine.createComponent(BodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);

        // create the data for the components and add them to the components
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        body.body = bodyFactory.newCircleBody(new Vector2(0.5f * width, 0.2f * height), 150f, BodyDef.BodyType.DynamicBody, false);

        position.position.set(10,10,0);
        texture.region = new TextureRegion(parent.assetManager.manager.get("badlogic.jpg", Texture.class));
        type.type = TypeComponent.ObjectType.OTHER;
        stateCom.set(StateComponent.State.NORMAL);
        body.body.setUserData(entity);

        // add the components to the entity
        entity.add(body);
        entity.add(position);
        entity.add(texture);
        entity.add(player);
        entity.add(colComp);
        entity.add(type);
        entity.add(stateCom);

        // add the entity to the engine
        engine.addEntity(entity);

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
        stage.dispose();
        background.dispose();
    }
}
