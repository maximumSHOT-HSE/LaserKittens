package ru.hse.team.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.algorithms.Geometry;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.BulletComponent;
import ru.hse.team.game.gamelogic.components.DoorComponent;
import ru.hse.team.game.gamelogic.components.TextureComponent;
import ru.hse.team.game.gamelogic.components.TransformComponent;
import ru.hse.team.game.levels.AbstractLevel;

/**
 * Draws game objects.
 * Draws all entities with Transform and Texture components.
 * Draws game status
 * Draws hints
 * Draws bullet trace
 *
 * Keeps camera object.
 * Makes camera following player and
 * doesn't allow it to move out of game borders
 */
public class RenderingSystem extends SortedIteratingSystem {

    // pixels per meter
    public static final float PPM = 32.0f;

    public static final float SCREEN_WIDTH = LaserKittens.getPreferredWidth() / PPM;
    public static final float SCREEN_HEIGHT = LaserKittens.getPreferredHeight() / PPM;

    private static final Vector2 METER_DIMENSIONS = new Vector2(SCREEN_WIDTH, SCREEN_HEIGHT);
    private static final Vector2 PIXEL_DIMENSIONS =
            new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    public static final float WIDTH_TO_METERS = METER_DIMENSIONS.x / PIXEL_DIMENSIONS.x;
    public static final float HEIGHT_TO_METERS = METER_DIMENSIONS.y / PIXEL_DIMENSIONS.y;

    public static Vector2 getScreenSizeInMeters() {
        return METER_DIMENSIONS;
    }

    public static Vector2 getScreenSizeInPixels() {
        return PIXEL_DIMENSIONS;
    }

    public static float pixelsToMetersWidth(float pixelValue){
        return pixelValue * SCREEN_WIDTH / Gdx.graphics.getWidth();
    }

    public static float pixelsToMetersHeight(float pixelValue){
        return pixelValue * SCREEN_HEIGHT / Gdx.graphics.getHeight();
    }


    public static float metersToPixelsWidth(float meterValue) {
        return meterValue * Gdx.graphics.getWidth() / SCREEN_WIDTH;
    }

    public static float metersToPixelsHeight(float meterValue) {
        return meterValue * Gdx.graphics.getHeight() / SCREEN_HEIGHT;
    }

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private ImmutableArray<Entity> renderQueue;

    private OrthographicCamera camera;
    private float cameraWaiting = 0;
    private Vector3 cameraMovingTo = new Vector3();

    private final LaserKittens laserKittens;
    private AbstractLevel abstractLevel;

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

    private void createCamera() {
        camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);
        camera.zoom = 1.5f;
        cameraMovingTo.set(camera.position);
    }

    public RenderingSystem(AbstractLevel abstractLevel, LaserKittens laserKittens) {
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

        this.abstractLevel = abstractLevel;
        this.batch = laserKittens.getBatch();
        this.shapeRenderer = laserKittens.getShapeRenderer();
        this.laserKittens = laserKittens;

        createCamera();
    }

    private void drawSegment(Vector2 from, Vector2 to, ShapeRenderer shapeRenderer, Color color) {
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(from, to, 0.2f);
    }

    private void drawBulletTrack() {
        for (Entity entity : renderQueue) {
            BulletComponent bulletComponent = Mapper.bulletComponent.get(entity);
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
            if (bulletComponent != null && bodyComponent != null) {
                Vector2 from = new Vector2(bulletComponent.path.get(0));
                for (Vector2 to : bulletComponent.path) {
                    drawSegment(from, to, shapeRenderer, Color.RED);
                    from.set(to);
                }
                drawSegment(from, bodyComponent.body.getPosition(), shapeRenderer, Color.RED);
            }
        }
    }

    private void drawHintsForDoors() {
        for (Entity entity : renderQueue) {
            DoorComponent doorComponent = Mapper.doorComponent.get(entity);
            BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
            if (doorComponent != null && bodyComponent != null &&
                doorComponent.getDoorHits() == 0) {
                Vector2 doorCenterPosition = bodyComponent.body.getPosition();
                Set<Entity> keyEntities = doorComponent.getKeys();
                for (Entity keyEntity : keyEntities) {
                    Body keyBody = Mapper.bodyComponent.get(keyEntity).body;
                    Vector2 keyPosition = keyBody.getPosition();
                    drawSegment(doorCenterPosition, keyPosition, shapeRenderer, Color.YELLOW); // DRAW HINT
                    if (abstractLevel.getAbstractGraph() != null) {
                        abstractLevel.getAbstractGraph().visit(keyPosition);
                    }
                }
            }
        }
    }

    private void drawFog() {
        if (abstractLevel.getAbstractGraph() != null) {
            List<Vector2> fogPositions = abstractLevel.getAbstractGraph().getFogPositions();
            float w = abstractLevel.getAbstractGraph().getVertexControlWidth() * 1.5f;
            float h = abstractLevel.getAbstractGraph().getVertexControlHeight() * 1.5f;
            for (Vector2 fogPosition : fogPositions) {
                Texture fogTexture = laserKittens.getAssetManager().getImage(KittensAssetManager.Images.FOG);
                batch.draw(fogTexture, fogPosition.x - w / 2, fogPosition.y - h / 2, w, h);
            }
        }
    }

    private void drawGraph() {
        if (abstractLevel.getAbstractGraph() != null && abstractLevel.getAbstractGraph().isDrawGraph()) {
            abstractLevel.getAbstractGraph().draw(shapeRenderer, batch);
        }
    }

    private void drawVisibleTexture(TextureComponent textureComponent,
                                    TransformComponent transformComponent) {
        float width = textureComponent.region.getRegionWidth();
        float height = textureComponent.region.getRegionHeight();
        float originX = width / 2f;
        float originY = height / 2f;

        float pixelHalfWidth = pixelsToMetersWidth(originX) * transformComponent.scale.x;
        float pixelHalfHeight = pixelsToMetersHeight(originY) * transformComponent.scale.y;

        if (Geometry.distanceToSegment(camera.position.x,
                transformComponent.position.x - pixelHalfWidth,
                transformComponent.position.x + pixelHalfWidth) >
                getScreenSizeInMeters().x * camera.zoom / 1.8) {
            return;
        }

        if (Geometry.distanceToSegment(camera.position.y,
                transformComponent.position.y - pixelHalfHeight,
                transformComponent.position.y + pixelHalfHeight) >
                getScreenSizeInMeters().y * camera.zoom / 1.8) {
            return;
        }

        batch.draw(textureComponent.region,
                transformComponent.position.x - originX,
                transformComponent.position.y - originY,
                originX, originY,
                width, height,
                pixelsToMetersWidth(transformComponent.scale.x),
                pixelsToMetersHeight(transformComponent.scale.y),
                transformComponent.rotation);
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

            drawVisibleTexture(texture, transformComponent);
        }

        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawBulletTrack();
        drawHintsForDoors();
        drawGraph();

        shapeRenderer.end();

        if (laserKittens.getPreferences().isEnabledFog()) {
            batch.begin();
            drawFog();
            batch.end();
        }

        if (laserKittens.getPreferences().isShowTime()) {
            abstractLevel.getGameStatus().draw(batch, laserKittens.getFont());
        }

        moveCamera(deltaTime);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {

    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    private static class ZComparator implements Comparator<Entity> {

        @Override
        public int compare(Entity entityA, Entity entityB) {
            float az = Mapper.transformComponent.get(entityA).position.z;
            float bz = Mapper.transformComponent.get(entityB).position.z;
            return Double.compare(az, bz);
        }
    }

    private void makeBordersForCamera(Vector3 position) {
        float levelWidth = SCREEN_WIDTH *
                abstractLevel.getLevelWidthInScreens();
        float levelHeight = SCREEN_HEIGHT *
                abstractLevel.getLevelHeightInScreens();

        position.x = Math.max(position.x,
                SCREEN_WIDTH * camera.zoom / 2 -
                        SCREEN_WIDTH / (2 * camera.zoom));
        position.y = Math.max(position.y,
                SCREEN_HEIGHT * camera.zoom / 2 -
                        SCREEN_HEIGHT / (2 * camera.zoom));
        position.x = Math.min(position.x,
                levelWidth - SCREEN_WIDTH* camera.zoom / 2 +
                        SCREEN_WIDTH / (2 * camera.zoom));
        position.y = Math.min(position.y,
                levelHeight - SCREEN_HEIGHT * camera.zoom / 2 +
                        SCREEN_HEIGHT / (2 * camera.zoom));
    }


    /**
     * Moves camera with speed depended exponentially from distance
     * */
    private void moveCamera(float delta) {
        delta = Math.max(delta, 0.1f); // when delta is near to zero problems occur
        final float speed = 2 * delta;
        final float ispeed = 1.0f-speed;

        Vector3 cameraPosition = new Vector3(camera.position);
        Entity player = abstractLevel.getPlayer();

        decreaseCameraWaitingTime(delta);

        if (player != null && getCameraWaiting() == 0) {
            TransformComponent playerTransform = Mapper.transformComponent.get(player);

            if (playerTransform == null) {
                return;
            }

            cameraMovingTo.set(playerTransform.position);
            makeBordersForCamera(cameraMovingTo);

            cameraPosition.scl(ispeed);
            cameraMovingTo.scl(speed);
            cameraPosition.add(cameraMovingTo);
            cameraMovingTo.scl(1f / speed);
            camera.position.set(cameraPosition);
        }

        makeBordersForCamera(camera.position);

        camera.update();
    }
}
