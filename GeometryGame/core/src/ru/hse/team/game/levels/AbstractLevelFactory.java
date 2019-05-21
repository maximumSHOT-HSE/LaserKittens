package ru.hse.team.game.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import javax.xml.soap.Text;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.BulletComponent;
import ru.hse.team.game.gamelogic.components.KeyComponent;
import ru.hse.team.game.gamelogic.components.StateComponent;
import ru.hse.team.game.gamelogic.components.TextureComponent;
import ru.hse.team.game.gamelogic.components.TransformComponent;
import ru.hse.team.game.gamelogic.components.TypeComponent;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;

abstract public class AbstractLevelFactory {

    protected BodyFactory bodyFactory;
    protected World world;
    protected PooledEngine engine;
    protected KittensAssetManager manager;

    protected int widthInScreens = 1;
    protected int heightInScreens = 1;

    protected Entity focusedPlayer;

    abstract public World getWorld();

    public Entity getPlayer() {
        return focusedPlayer;
    }

    public AbstractLevelFactory() {

    }

    public AbstractLevelFactory(int widthInScreens, int heightInScreens) {
        setLevelSize(widthInScreens, heightInScreens);
    }

    public void setLevelSize(int widthInScreens, int heightInScreens) {
        this.widthInScreens = widthInScreens;
        this.heightInScreens = heightInScreens;
    }

    public int getLevelWidthInScreens() {
        return widthInScreens;
    }

    public int getLevelHeightInScreens() {
        return heightInScreens;
    }

    abstract public void createLevel(PooledEngine engine, KittensAssetManager assetManager);

    public Entity createStar(float x, float y, float radius) {
        Texture texture = manager.manager.get(KittensAssetManager.Star2, Texture.class);
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

    protected Entity createLaser(Vector2 source, Vector2 direction, long lifeTime) {

        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newBullet(source, direction))
                .addTransformComponent(new Vector3(source.x, source.y, 50))
                .addTextureComponent(null)
                .addTypeComponent(TypeComponent.Type.BULLET)
                .addStateComponent(StateComponent.State.NORMAL)
                .addBulletComponent(System.currentTimeMillis(), lifeTime, source)
                .build();
    }

    protected Entity createBackground() {
        Texture background = manager.manager.get("blue-background.jpg", Texture.class);
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
        Texture texture = new Texture(KittensAssetManager.MIRROR);
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
                manager.manager.get(KittensAssetManager.Cat3, Texture.class).getHeight() * 0.78f * 0.5f);
        Vector2 scale = new Vector2(radius / regionCatRadius, radius / regionCatRadius);

        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newPlayerBody(new Vector2(playerX, playerY), radius))
                .addTransformComponent(new Vector3(playerX, playerY, 100), scale, 0, false)
                .addTextureComponent(new TextureRegion(manager.manager.get(KittensAssetManager.Cat3, Texture.class)))
                .addStateComponent(StateComponent.State.NORMAL)
                .addTypeComponent(TypeComponent.Type.PLAYER)
                .build();
    }

    protected Entity createDisappearingWall(Vector2 center, float width, float height) {
        return (new EntityBuilder())
            .addBodyComponent(bodyFactory.newRectangle(center, width, height))
            .addTransformComponent(new Vector3(center.x, center.y, 10))
            .addTextureComponent(null)
            .addTypeComponent(TypeComponent.Type.DISAPPEARING_WALL)
            .addStateComponent(StateComponent.State.JUST_CREATED)
            .build();
    }

    protected Entity createImpenetrableWall(Vector2 center, float width, float height) {
        Texture texture = new Texture(KittensAssetManager.ICE_WALL);
        texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion textureRegion = new TextureRegion(
                texture, 0, 0,
                (int) RenderingSystem.metersToPixels(width),
                (int) RenderingSystem.metersToPixels(height)
        );
        return (new EntityBuilder())
            .addBodyComponent(bodyFactory.newRectangle(center, width, height))
            .addTransformComponent(new Vector3(center.x, center.y, 10))
            .addTextureComponent(textureRegion)
            .addTypeComponent(TypeComponent.Type.IMPENETRABLE_WALL)
            .build();
    }

    protected Entity createDoor(Vector2 center, float width, float height) {
        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newRectangle(center, width, height))
                .addTransformComponent(new Vector3(center.x, center.y, 10))
                .addTextureComponent(null)
                .addTypeComponent(TypeComponent.Type.IMPENETRABLE_WALL)
                .addStateComponent(StateComponent.State.NORMAL)
                .build();
    }

    protected Entity createKey(Vector2 center, float width, float height, Entity door) {
        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newRectangle(center, width, height))
                .addTransformComponent(new Vector3(center.x, center.y, 10))
                .addTextureComponent(null)
                .addTypeComponent(TypeComponent.Type.KEY)
                .addStateComponent(StateComponent.State.NORMAL)
                .addKeyComponent(door)
                .build();
    }

    protected Entity createPointer(Vector2 position, float rotation) {
        Texture texture = manager.manager.get(KittensAssetManager.Pointer, Texture.class);
        return (new EntityBuilder())
                .addTransformComponent(
                        new Vector3(position, 0),
                        new Vector2(1,1),
                        rotation, false)
                .addTextureComponent(new TextureRegion(texture))
                .build();
    }

    protected Entity createQuestion(Vector2 position, float scale) {
        Texture textture = manager.manager.get(KittensAssetManager.Question, Texture.class);
        return (new EntityBuilder())
                .addTransformComponent(new Vector3(position, 0), new Vector2(scale, scale), 0f, false)
                .addTextureComponent(new TextureRegion(textture))
                .build();
    }

    protected Entity createTransparentWall(Vector2 center, float width, float height) {
        Texture texture = new Texture(KittensAssetManager.TRANSPARENT_WALL);
        texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion textureRegion = new TextureRegion(
                texture, 0, 0,
                (int) RenderingSystem.metersToPixels(width),
                (int) RenderingSystem.metersToPixels(height)
        );
        return (new EntityBuilder())
                .addBodyComponent(bodyFactory.newTransparentRectangle(center, width, height))
                .addTransformComponent(new Vector3(center.x, center.y, 8))
                .addTextureComponent(textureRegion)
                .addTypeComponent(TypeComponent.Type.TRANSPARENT_WALL)
                .build();
    }

    public float getPlayerRadius() {
        Body playerBody = Mapper.bodyComponent.get(getPlayer()).body;
        return playerBody.getFixtureList().get(0).getShape().getRadius();
    }

    protected class EntityBuilder {
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

        public EntityBuilder addBulletComponent(long creationTime, long lifeTime, Vector2 source) {
            BulletComponent bulletComponent = engine.createComponent(BulletComponent.class);
            bulletComponent.creationTime = creationTime;
            bulletComponent.lifeTime = lifeTime;
            bulletComponent.path.add(source);
            entity.add(bulletComponent);
            return this;
        }

        public EntityBuilder addStateComponent(StateComponent.State state) {
            StateComponent stateComponent = engine.createComponent(StateComponent.class);
            stateComponent.set(state);
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
            return this;
        }
    }

}