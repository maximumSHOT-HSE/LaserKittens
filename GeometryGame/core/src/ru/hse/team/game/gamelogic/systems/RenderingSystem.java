package ru.hse.team.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.BulletComponent;
import ru.hse.team.game.gamelogic.components.TextureComponent;
import ru.hse.team.game.gamelogic.components.TransformComponent;

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
    private ImmutableArray<Entity> renderQueue;
    private Comparator<Entity> comparator = new ZComparator();
    private OrthographicCamera camera;
    private float cameraWaiting = 0;

    public void decreaseCameraWaitingTime(float deltaTime) {
        cameraWaiting -= deltaTime;
        if (cameraWaiting < 0) {
            cameraWaiting = 0;
        }
    }

    public float getCameraWaiting() {
        return cameraWaiting;
    }

    public void setCameraWaiting(float cameraWaiting) {
        this.cameraWaiting = cameraWaiting;
    }

    @SuppressWarnings("unchecked")
    public RenderingSystem(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        this.batch = batch;
        this.shapeRenderer = shapeRenderer;

        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);
    }

    private void drawSegment(Vector2 from, Vector2 to, ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rectLine(from, to, 0.1f);
    }

    private float distance2D(Vector3 positionA, Vector3 positionB) {
        return (float)Math.sqrt((positionA.x - positionB.x) * (positionA.x - positionB.x) + (positionA.y - positionB.y) * (positionA.y - positionB.y));
    }
    private float length2D(Vector2 a) {
        return (float)Math.sqrt(a.x * a.x + a.y * a.y);
    }

    private float distanceToStrip(float a, float left, float right) {
        float distance = 0;

        if (a < left) {
            distance = Math.max(distance, left - a);
        }
        if (a > right) {
            distance = Math.max(distance, a - right);
        }
        return distance;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        renderQueue = getEntities();

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

            float pixelHalfWidth = pixelsToMeters(originX) * transformComponent.scale.x;
            float pixelHalfHeight = pixelsToMeters(originY) * transformComponent.scale.y;

            if (distanceToStrip(camera.position.x, transformComponent.position.x - pixelHalfWidth,
                    transformComponent.position.x + pixelHalfWidth) > getScreenSizeInMeters().x * camera.zoom / 1.8) {
                continue;
            }

            if (distanceToStrip(camera.position.y, transformComponent.position.y - pixelHalfHeight,
                    transformComponent.position.y + pixelHalfHeight) > getScreenSizeInMeters().y * camera.zoom / 1.8) {
                continue;
            }


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

    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {

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
