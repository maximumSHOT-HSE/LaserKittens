package com.example.learning.game.gamelogic.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.example.learning.game.Mapper;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.BulletComponent;
import com.example.learning.game.gamelogic.components.TextureComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;

import java.lang.invoke.VolatileCallSite;
import java.util.Comparator;
import java.util.Map;

public class RenderingSystem extends SortedIteratingSystem {

    public static final float PPM = 32.0f; // sets the amount of pixels each metre of box2d objects contains

    // this gets the height and width of our camera frustrum based off the width and height of the screen and our pixel per meter ratio
    static final float FRUSTUM_WIDTH = Gdx.graphics.getWidth()/PPM;
    static final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight()/PPM;

    public static final float PIXELS_TO_METRES = 1.0f / PPM; // get the ratio for converting pixels to metres

    // static method to get screen width in metres
    private static Vector2 meterDimensions = new Vector2();
    private static Vector2 pixelDimensions = new Vector2();

    public static Vector2 getScreenSizeInMeters() {
        meterDimensions.set(Gdx.graphics.getWidth()*PIXELS_TO_METRES,
                Gdx.graphics.getHeight()*PIXELS_TO_METRES);
        return meterDimensions;
    }

    // static method to get screen size in pixels
    public static Vector2 getScreenSizeInPixels() {
        pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return pixelDimensions;
    }

    // convenience method to convert pixels to meters
    public static float PixelsToMeters(float pixelValue){
        return pixelValue * PIXELS_TO_METRES;
    }

    public static float MetersToPixels(float meterValue) {
        return meterValue / PIXELS_TO_METRES;
    }

    private SpriteBatch batch; // a reference to our spritebatch
    private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of each other
    private Comparator<Entity> comparator = new ZComparator(); // a comparator to sort images based on the z position of the transfromComponent
    private OrthographicCamera camera; // a reference to our camera

    // component mappers to get components from entities
    private ComponentMapper<TextureComponent> textureM;
    private ComponentMapper<TransformComponent> transformM;

    @SuppressWarnings("unchecked")
    public RenderingSystem(SpriteBatch batch) {
        // gets all entities with a TransofmComponent and TextureComponent
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        //creates out componentMappers
        textureM = ComponentMapper.getFor(TextureComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);

        // create the array for sorting entities
        renderQueue = new Array<Entity>();

        this.batch = batch;  // set our batch to the one supplied in constructor

        // set up the camera to match our screen size
        camera = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        camera.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);

    }

    private void drawSegment(Vector2 from, Vector2 to, ShapeRenderer shapeRenderer) {
        float dx = to.x - from.x;
        float dy = to.y - from.y;
        float dist = (float) Math.sqrt(dx * dx + dy *dy);
        float angle = (float) Math.atan2(dy, dx);
        System.out.println("PRINT! (" + RenderingSystem.MetersToPixels(from.x) +
                ", " +
                RenderingSystem.MetersToPixels(from.y) +
                ") - (" +
                RenderingSystem.MetersToPixels(to.x) +
                ", " +
                RenderingSystem.MetersToPixels(to.y) +
        ")");
//        from = new Vector2(10, 10);
//        to = new Vector2(500, 500);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(from, to);
//        shapeRenderer.line(
//            new Vector2(
//                RenderingSystem.MetersToPixels(from.x),
//                RenderingSystem.MetersToPixels(from.y)
//            ),
//            new Vector2(
//                RenderingSystem.MetersToPixels(to.x),
//                RenderingSystem.MetersToPixels(to.y)
//            )
//        );
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // sort the renderQueue based on z index
        renderQueue.sort(comparator);

        // update camera and sprite batch
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        batch.begin();

        // loop through each entity in our render queue
        for (Entity entity : renderQueue) {
            TextureComponent tex = textureM.get(entity);
            TransformComponent t = transformM.get(entity);

            if (tex.region == null || t.isHidden) {
                continue;
            }

            float width = tex.region.getRegionWidth();
            float height = tex.region.getRegionHeight();

            float originX = width/2f;
            float originY = height/2f;

            batch.draw(tex.region,
                    t.position.x - originX, t.position.y - originY,
                    originX, originY,
                    width, height,
                    PixelsToMeters(t.scale.x), PixelsToMeters(t.scale.y),
                    t.rotation);
        }

        batch.end();

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Entity entity : renderQueue) {
            BulletComponent bulletComponent = Mapper.bulletComponent.get(entity);
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);

            if (bulletComponent != null && bodyComponent != null) {
                Vector2 from;
                from = bulletComponent.path.get(0);
                for (Vector2 to : bulletComponent.path) {
                    drawSegment(from, to, shapeRenderer);
//                    from.x = to.x;
//                    from.y = to.y;
                }
            }
        }

        shapeRenderer.end();

        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    // convenience method to get camera
    public OrthographicCamera getCamera() {
        return camera;
    }

    private static class ZComparator implements Comparator<Entity> {
        private ComponentMapper<TransformComponent> cmTrans;

        private ZComparator(){
            cmTrans = ComponentMapper.getFor(TransformComponent.class);
        }

        @Override
        public int compare(Entity entityA, Entity entityB) {
            float az = cmTrans.get(entityA).position.z;
            float bz = cmTrans.get(entityB).position.z;
            return Double.compare(az, bz);
        }
    }

}
