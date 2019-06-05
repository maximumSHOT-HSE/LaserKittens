package ru.hse.team.game.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.BulletComponent;
import ru.hse.team.game.gamelogic.components.DoorComponent;
import ru.hse.team.game.gamelogic.components.KeyComponent;
import ru.hse.team.game.gamelogic.components.MessageComponent;
import ru.hse.team.game.gamelogic.components.PatrolComponent;
import ru.hse.team.game.gamelogic.components.StateComponent;
import ru.hse.team.game.gamelogic.components.TextureComponent;
import ru.hse.team.game.gamelogic.components.TransformComponent;
import ru.hse.team.game.gamelogic.components.TumblerComponent;
import ru.hse.team.game.gamelogic.components.TypeComponent;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;

abstract public class AbstractLevelFactory {

    private int currentEntityId = 0;
    private Map<Integer, Entity> idToEntity = new HashMap<>();

    private PooledEngine engine;
    private KittensAssetManager manager;
    private BodyFactory bodyFactory;
    private List<Barrier> barriers = new ArrayList<>();

    public AbstractLevelFactory(
            PooledEngine engine, KittensAssetManager manager, BodyFactory bodyFactory) {
        this.engine = engine;
        this.manager = manager;
        this.bodyFactory = bodyFactory;
    }

    abstract public void createLevel(int widthInScreens, int heightInScreens,
                                     AbstractLevel abstractLevel);


    public Entity createStar(float x, float y, float radius) {
        Texture texture = manager.getImage(KittensAssetManager.Images.STAR_2);
        Vector2 scale = new Vector2(2 * radius / RenderingSystem.pixelsToMeters(texture.getWidth()),
                2 * radius / RenderingSystem.pixelsToMeters(texture.getHeight()));

        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newStar(new Vector2(x, y), radius, BodyDef.BodyType.DynamicBody, false))
                .addTransformComponent(new Vector3(x, y, 0), scale, 0, false)
                .addTypeComponent(TypeComponent.Type.STAR)
                .addStateComponent(StateComponent.State.JUST_CREATED)
                .addTextureComponent(new TextureRegion(texture))
                .build();
    }

    public Entity createLaser(Vector2 source, Vector2 direction, int lifeTime) {

        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newBullet(source, direction))
                .addTransformComponent(new Vector3(source.x, source.y, 50))
                .addTextureComponent(null)
                .addTypeComponent(TypeComponent.Type.BULLET)
                .addStateComponent(StateComponent.State.JUST_CREATED)
                .addBulletComponent(System.currentTimeMillis(), lifeTime, source)
                .build();
    }

    protected Entity createBackground(int widthInScreens, int heightInScreens) {
        Texture background = manager.getImage(KittensAssetManager.Images.BLUE_BACKGROUND);
        background.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion backgroundRegion = new TextureRegion(background);

        Vector3 position = new Vector3(widthInScreens * RenderingSystem.getScreenSizeInMeters().x / 2,
                heightInScreens * RenderingSystem.getScreenSizeInMeters().y / 2,
                -1e9f);
        Vector2 scale = new Vector2( RenderingSystem.getScreenSizeInPixels().x / backgroundRegion.getRegionWidth(),
                RenderingSystem.getScreenSizeInPixels().y / backgroundRegion.getRegionHeight());
        backgroundRegion.setRegion(0, 0, background.getWidth() * (widthInScreens + 2),
                background.getHeight() * (heightInScreens + 2));

        return (new EntityBuilder())
                .addTransformComponent(position, scale, 0, false)
                .addTextureComponent(backgroundRegion)
                .build();
    }

    protected Entity createMirror(Vector2 center, float width, float height, float rotation) {
        Texture texture = manager.getImage(KittensAssetManager.Images.MIRROR);
        texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion textureRegion = new TextureRegion(
                texture, 0, 0,
                (int) RenderingSystem.metersToPixels(width),
                (int) RenderingSystem.metersToPixels(height)
        );
        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newRectangle(center, width, height, rotation))
                .addTransformComponent(new Vector3(center.x, center.y, 0))
                .addTextureComponent(textureRegion)
                .addTypeComponent(TypeComponent.Type.MIRROR)
                .build();
    }

    protected Entity createMirror(Vector2 center, float width, float height) {
        return createMirror(center, width, height, 0);
    }

    protected Entity createPlayer(float playerX, float playerY, float radius) {

        float regionCatRadius = RenderingSystem.pixelsToMeters(
                manager.getImage(KittensAssetManager.Images.CAT_3).getHeight() * 0.78f * 0.5f);
        Vector2 scale = new Vector2(radius / regionCatRadius, radius / regionCatRadius);

        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newPlayerBody(new Vector2(playerX, playerY), radius))
                .addTransformComponent(new Vector3(playerX, playerY, 100), scale, 0, false)
                .addTextureComponent(new TextureRegion(manager.getImage(KittensAssetManager.Images.CAT_3)))
                .addStateComponent(StateComponent.State.JUST_CREATED)
                .addTypeComponent(TypeComponent.Type.PLAYER)
                .build();
    }

    protected Entity createGuardian(float guardianX, float guardianY, float radius, List<Vector2> path, float velocty) {
        float regionGuardianRadius = RenderingSystem.pixelsToMeters(
                manager.getImage(KittensAssetManager.Images.CAT_2).getHeight() * 0.78f * 0.5f);
        Vector2 scale = new Vector2(radius / regionGuardianRadius, radius / regionGuardianRadius);

        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newGuardianBody(new Vector2(guardianX, guardianY), radius))
                .addTransformComponent(new Vector3(guardianX, guardianY, 100), scale, 0, false)
                .addTextureComponent(new TextureRegion(manager.getImage(KittensAssetManager.Images.CAT_2)))
                .addStateComponent(StateComponent.State.JUST_CREATED)
                .addTypeComponent(TypeComponent.Type.GUARDIAN)
                .addPatrolComponent(path, velocty)
                .build();
    }

    protected Entity createImpenetrableWall(Vector2 center, float width, float height) {
        return createImpenetrableWall(center, width, height, 0);
    }

    protected Entity createImpenetrableWall(Vector2 center, float width, float height, float rotation) {
        Texture texture = manager.getImage(KittensAssetManager.Images.ICE_WALL);
        texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion textureRegion = new TextureRegion(
                texture, 0, 0,
                (int) RenderingSystem.metersToPixels(width),
                (int) RenderingSystem.metersToPixels(height)
        );
        Entity wall = (new EntityBuilder())
                .addBodyComponent(bodyFactory.newRectangle(center, width, height, rotation))
                .addTransformComponent(new Vector3(center.x, center.y, 10))
                .addTextureComponent(textureRegion)
                .addTypeComponent(TypeComponent.Type.IMPENETRABLE_WALL)
                .addStateComponent(StateComponent.State.JUST_CREATED)
                .build();

        addBarrier(center, width, height, Mapper.stateComponent.get(wall).getId());

        return wall;
    }

    protected Entity createImpenetrableDynamicWall(Vector2 center, float width, float height) {
        Texture texture = manager.getImage(KittensAssetManager.Images.ICE_WALL);
        texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion textureRegion = new TextureRegion(
                texture, 0, 0,
                (int) RenderingSystem.metersToPixels(width),
                (int) RenderingSystem.metersToPixels(height)
        );
        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newDynamicRectangle(center, width, height, 0))
                .addTransformComponent(new Vector3(center.x, center.y, 10))
                .addTextureComponent(textureRegion)
                .addTypeComponent(TypeComponent.Type.IMPENETRABLE_WALL)
                .addStateComponent(StateComponent.State.JUST_CREATED)
                .build();
    }

    protected Entity createDoor(Vector2 center, float width, float height) {
        Texture texture = manager.getImage(KittensAssetManager.Images.DOOR);
        texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion textureRegion = new TextureRegion(
                texture, 0, 0,
                (int) RenderingSystem.metersToPixels(width),
                (int) RenderingSystem.metersToPixels(height)
        );
        Entity door = (new EntityBuilder())
                .addBodyComponent(bodyFactory.newRectangle(center, width, height))
                .addTransformComponent(new Vector3(center.x, center.y, 5))
                .addTextureComponent(textureRegion)
                .addTypeComponent(TypeComponent.Type.DOOR)
                .addStateComponent(StateComponent.State.JUST_CREATED)
                .addDoorComponent()
                .build();

        addBarrier(center, width, height, Mapper.stateComponent.get(door).getId());
        System.out.println("CREATE DOOR with id = " + Mapper.stateComponent.get(door).getId());
        System.out.flush();
        return door;
    }

    protected Entity createKey(Vector2 center, float width, float height, Entity door) {
        Texture texture = manager.getImage(KittensAssetManager.Images.KEY);
        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newRectangle(center, width, height))
                .addTransformComponent(new Vector3(center.x, center.y, 10))
                .addTextureComponent(new TextureRegion(texture))
                .addTypeComponent(TypeComponent.Type.KEY)
                .addStateComponent(StateComponent.State.JUST_CREATED)
                .addKeyComponent(door)
                .build();
    }

    protected Entity createPointer(Vector2 position, float rotation) {
        Texture texture = manager.getImage(KittensAssetManager.Images.POINTER);
        return (new EntityBuilder())
                .addTransformComponent(
                        new Vector3(position, 0),
                        new Vector2(1,1),
                        rotation, false)
                .addTextureComponent(new TextureRegion(texture))
                .build();
    }

    protected Entity createQuestion(Vector2 position, float scale, String message) {
        TextureRegion texture = new TextureRegion(manager.getImage(KittensAssetManager.Images.QUESTION));
        float width = RenderingSystem.pixelsToMeters(texture.getRegionWidth()) * scale;
        float height = RenderingSystem.pixelsToMeters(texture.getRegionHeight()) * scale;

        return (new EntityBuilder())
                .addTransformComponent(new Vector3(position, 0), new Vector2(scale, scale), 0f, false)
                .addTextureComponent(texture)
                .addTypeComponent(TypeComponent.Type.QUESTION)
                .addBodyComponent(bodyFactory.newSensorRectangle(position, width, height, 0))
                .addMessageComponent(message)
                .build();
    }

    protected Entity createTransparentWall(Vector2 center, float width, float height) {
        return createTransparentWall(center, width, height, 0);
    }

    protected Entity createTransparentWall(Vector2 center, float width, float height, float rotation) {
        Texture texture = manager.getImage(KittensAssetManager.Images.TRANSPARENT_WALL);
        texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion textureRegion = new TextureRegion(
                texture, 0, 0,
                (int) RenderingSystem.metersToPixels(width),
                (int) RenderingSystem.metersToPixels(height)
        );
        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newTransparentRectangle(center, width, height, rotation))
                .addTransformComponent(new Vector3(center.x, center.y, 8))
                .addTextureComponent(textureRegion)
                .addTypeComponent(TypeComponent.Type.TRANSPARENT_WALL)
                .build();
    }

    protected Entity createTumbler(
            Vector2 center,
            float width,
            float height,
            Runnable task) {
        Texture texture = manager.getImage(KittensAssetManager.Images.YELLOW_TUMBLER);
        TextureRegion textureRegion = new TextureRegion(
                texture, 0, 0,
                (int) RenderingSystem.metersToPixels(width),
                (int) RenderingSystem.metersToPixels(height)
        );
        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newRectangle(center, width, height, 0))
                .addTransformComponent(new Vector3(center, 0))
                .addTextureComponent(textureRegion)
                .addTumblerComponent(task)
                .addTypeComponent(TypeComponent.Type.TUMBLER)
                .build();
    }

    public void setOpponentPosition(Vector2 source) {

    }

    public class EntityBuilder {
        private Entity entity = engine.createEntity();

        public Entity build() {
            engine.addEntity(entity);
            return entity;
        }

        public EntityBuilder addBodyComponent(Body body) {
            BodyComponent bodyComponent = engine.createComponent(BodyComponent.class);
            bodyComponent.body = body;
            bodyComponent.body.setUserData(entity);
            entity.add(bodyComponent);
            return this;
        }

        public EntityBuilder addMessageComponent(String message) {
            MessageComponent messageComponent = engine.createComponent(MessageComponent.class);
            messageComponent.message = message;
            entity.add(messageComponent);
            return this;
        }

        public EntityBuilder addBulletComponent(long creationTime, int lifeTime, Vector2 source) {
            BulletComponent bulletComponent = engine.createComponent(BulletComponent.class);
            bulletComponent.creationTime = creationTime;
            bulletComponent.lifeTime = lifeTime;
            bulletComponent.path.add(source);
            entity.add(bulletComponent);
            return this;
        }

        public EntityBuilder addPatrolComponent(List<Vector2> path, float velocity) {
            PatrolComponent patrolComponent = engine.createComponent(PatrolComponent.class);
            patrolComponent.setPath(path);
            patrolComponent.setEntity(entity);
            patrolComponent.setVelocity(velocity);
            entity.add(patrolComponent);
            return this;
        }

        public EntityBuilder addStateComponent(StateComponent.State state) {
            StateComponent stateComponent = engine.createComponent(StateComponent.class);
            stateComponent.set(state);
            stateComponent.setId(currentEntityId);
            idToEntity.put(currentEntityId, entity);
            currentEntityId++;
            entity.add(stateComponent);
            return this;
        }

        public EntityBuilder addTextureComponent(TextureRegion textureRegion) {
            TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
            textureComponent.region = textureRegion;
            entity.add(textureComponent);
            return this;
        }

        public EntityBuilder addTransformComponent(Vector3 position) {
            TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
            transformComponent.position.set(position);
            entity.add(transformComponent);
            return this;
        }

        public EntityBuilder addTransformComponent(Vector3 position, Vector2 scale, float rotation, boolean isHidden) {
            TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
            transformComponent.position.set(position);
            transformComponent.scale.set(scale);
            transformComponent.rotation = rotation;
            transformComponent.isHidden = isHidden;
            entity.add(transformComponent);
            return this;
        }

        public EntityBuilder addTypeComponent(TypeComponent.Type type) {
            TypeComponent typeComponent = engine.createComponent(TypeComponent.class);
            typeComponent.type = type;
            entity.add(typeComponent);
            return this;
        }

        public EntityBuilder addKeyComponent(Entity door) {
            KeyComponent keyComponent = engine.createComponent(KeyComponent.class);
            keyComponent.door = door;
            entity.add(keyComponent);
            Mapper.doorComponent.get(door).addKey(entity);
            return this;
        }

        public EntityBuilder addDoorComponent() {
            DoorComponent doorComponent = engine.createComponent(DoorComponent.class);
            entity.add(doorComponent);
            return this;
        }

        public EntityBuilder addTumblerComponent(Runnable task) {
            TumblerComponent tumblerComponent = engine.createComponent(TumblerComponent.class);
            tumblerComponent.setAction(task);
            entity.add(tumblerComponent);
            return this;
        }
    }

    /*
    * Places the impenetrable wall to the given part of screen
    * and stores that wall in the level's barrier storage
    */
    protected Entity placeImpenetrableWall(
            float relativeX,
            float relativeY,
            float relativeWidth,
            float relativeHeight) {
        Vector2 center = new Vector2(
                relativeX * RenderingSystem.getScreenSizeInMeters().x,
                relativeY * RenderingSystem.getScreenSizeInMeters().y
        );
        float width = relativeWidth * RenderingSystem.getScreenSizeInMeters().x;
        float height = relativeHeight * RenderingSystem.getScreenSizeInMeters().y;
        Entity wall = createImpenetrableWall(center, width, height);
        return wall;
    }

    protected Entity placeDynamicImpenetrableWall(
            float relativeX,
            float relativeY,
            float relativeWidth,
            float relativeHeight) {
        Vector2 center = new Vector2(
                relativeX * RenderingSystem.getScreenSizeInMeters().x,
                relativeY * RenderingSystem.getScreenSizeInMeters().y
        );
        float width = relativeWidth * RenderingSystem.getScreenSizeInMeters().x;
        float height = relativeHeight * RenderingSystem.getScreenSizeInMeters().y;
        Entity wall = createImpenetrableDynamicWall(center, width, height);
        addBarrier(center, width, height, Mapper.stateComponent.get(wall).getId());
        return wall;
    }

    protected void addAngularVelocity(Entity entity, float angularVelocity) {
        entity.getComponent(BodyComponent.class).body.setAngularVelocity(angularVelocity);
    }

    protected Entity placeTransparentWall(
            float relativeX,
            float relativeY,
            float relativeWidth,
            float relativeHeight) {
        return createTransparentWall(
                new Vector2(
                        relativeX * RenderingSystem.getScreenSizeInMeters().x,
                        relativeY * RenderingSystem.getScreenSizeInMeters().y
                ),
                relativeWidth * RenderingSystem.getScreenSizeInMeters().x,
                relativeHeight * RenderingSystem.getScreenSizeInMeters().y
        );
    }

    /*
     * Places the door to the given part of screen
     * and stores that door in the level's barrier storage
     */
    protected Entity placeDoor(
            float relativeX,
            float relativeY,
            float relativeWidth,
            float relativeHeight) {
        Vector2 center = new Vector2(
                relativeX * RenderingSystem.getScreenSizeInMeters().x,
                relativeY * RenderingSystem.getScreenSizeInMeters().y
        );
        float width = relativeWidth * RenderingSystem.getScreenSizeInMeters().x;
        float height = relativeHeight * RenderingSystem.getScreenSizeInMeters().y;
        return createDoor(center, width, height);
    }

    protected Entity placePointer(
            float relativeX,
            float relativeY,
            float rotation) {
        return createPointer(
                new Vector2(
                        relativeX * RenderingSystem.getScreenSizeInMeters().x,
                        relativeY * RenderingSystem.getScreenSizeInMeters().y
                ),
                rotation
        );
    }

    protected Entity placeQuestion(float relativeX, float relativeY, float scale, String message) {
        return createQuestion(new Vector2(
                        relativeX * RenderingSystem.getScreenSizeInMeters().x,
                        relativeY * RenderingSystem.getScreenSizeInMeters().y),
                scale, message);
    }

    protected Entity placeMirror(
            float relativeX,
            float relativeY,
            float relativeWidth,
            float relativeHeight,
            float rotation) {
        return createMirror(
                new Vector2(
                        relativeX * RenderingSystem.getScreenSizeInMeters().x,
                        relativeY * RenderingSystem.getScreenSizeInMeters().y
                ),
                relativeWidth * RenderingSystem.getScreenSizeInMeters().x,
                relativeHeight * RenderingSystem.getScreenSizeInMeters().y,
                rotation
        );
    }

    public void removeKey(int keyId) {
        System.out.println("REMOVE key with id = " + keyId);
        Entity key = idToEntity.get(keyId);
        if (key == null) {
            return;
        }
        idToEntity.remove(keyId);
        StateComponent stateComponent = Mapper.stateComponent.get(key);
        if (stateComponent == null) {
            return;
        }
        System.out.println("THERE IS STATE");
        stateComponent.finish();
    }

    public PooledEngine getEngine() {
        return engine;
    }

    public KittensAssetManager getManager() {
        return manager;
    }

    public BodyFactory getBodyFactory() {
        return bodyFactory;
    }

    public void addBarrier(Vector2 center, float width, float height, int id) {
        barriers.add(new Barrier(center, width, height, id));
    }

    public List<Barrier> getBarriers() {
        return barriers;
    }

    /**
     * Barrier is the entity through which player can not pass.
     * It has center, width and height
     */
    public class Barrier {

        private Vector2 center;
        private float width;
        private float height;
        private int id;

        public Barrier(Vector2 center, float width, float height, int id) {
            this.center = center;
            this.width = width;
            this.height = height;
            this.id = id;
        }

        public Vector2 getCenter() {
            return center;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public int getId() {
            return id;
        }
    }
}
