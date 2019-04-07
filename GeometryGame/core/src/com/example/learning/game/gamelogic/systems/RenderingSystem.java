package com.example.learning.game.gamelogic.systems;

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

import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {

    // pixels per meter
    public static final float PPM = 32.0f;

    public static final float SCREEN_WIDTH = Gdx.graphics.getWidth()/PPM;
    public static final float SCREEN_HEIGHT = Gdx.graphics.getHeight()/PPM;

    public static final float PIXELS_TO_METRES = 1.0f / PPM;

    private static Vector2 meterDimensions = new Vector2(SCREEN_WIDTH, SCREEN_HEIGHT);
    private static Vector2 pixelDimensions = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    public static Vector2 getScreenSizeInMeters() {
        return meterDimensions;
    }

    public static Vector2 getScreenSizeInPixels() {
        return pixelDimensions;
    }

    public static float pixelsToMeters(float pixelValue){
        return pixelValue * PIXELS_TO_METRES;
    }

    public static float metersToPixels(float meterValue) {
        return meterValue / PIXELS_TO_METRES;
    }

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Array<Entity> renderQueue;
    private Comparator<Entity> comparator = new ZComparator();
    private OrthographicCamera camera;

    @SuppressWarnings("unchecked")
    public RenderingSystem(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());
        renderQueue = new Array<>();

        this.batch = batch;
        this.shapeRenderer = shapeRenderer;

        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);

    }

    private void drawSegment(Vector2 from, Vector2 to, ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rectLine(from, to, 0.1f);

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        renderQueue.sort(comparator);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.enableBlending();
        batch.begin();

        for (Entity entity : renderQueue) {
            TextureComponent texture = Mapper.textureComponent.get(entity);
            TransformComponent transformComponent = Mapper.transformComponent.get(entity);

            if (texture.region == null || transformComponent.isHidden) {
                continue;
            }

            float width = texture.region.getRegionWidth();
            float height = texture.region.getRegionHeight();

            float originX = width / 2f;
            float originY = height / 2f;

            batch.draw(texture.region,
                    transformComponent.position.x - originX, transformComponent.position.y - originY,
                    originX, originY,
                    width, height,
                    pixelsToMeters(transformComponent.scale.x), pixelsToMeters(transformComponent.scale.y),
                    transformComponent.rotation);
        }

        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Entity entity : renderQueue) {
            BulletComponent bulletComponent = Mapper.bulletComponent.get(entity);
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
            if (bulletComponent != null && bodyComponent != null) {
                Vector2 from = new Vector2(bulletComponent.path.get(0));
                for (Vector2 to : bulletComponent.path) {
                    drawSegment(from, to, shapeRenderer);
                    from.set(to);
                }
                drawSegment(from, bodyComponent.body.getPosition(), shapeRenderer);
            }
        }

        shapeRenderer.end();

        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    private static class ZComparator implements Comparator<Entity> {

        private ZComparator() {

        }

        @Override
        public int compare(Entity entityA, Entity entityB) {
            float az = Mapper.transformComponent.get(entityA).position.z;
            float bz = Mapper.transformComponent.get(entityB).position.z;
            return Double.compare(az, bz);
        }
    }
}
