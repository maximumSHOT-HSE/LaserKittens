package ru.hse.team.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import ru.hse.team.game.Multiplayer.AppWarp.WarpController;
import ru.hse.team.game.Multiplayer.MessageCreator;
import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.BulletComponent;
import ru.hse.team.game.gamelogic.components.TypeComponent;
import ru.hse.team.game.levels.AbstractLevel;

public class ContactProcessor implements ContactListener {

    private AbstractLevel abstractLevel;

    public ContactProcessor(AbstractLevel abstractLevel) {
        this.abstractLevel = abstractLevel;
    }

    /*
     * Checks whether data is bullet (iff body and bullet components exists)
     * or not and
     * if yes then current body position will be added
     * to path of bullet
     * */
    private void processBullet(Entity entity) {
        BulletComponent bulletComponent = Mapper.bulletComponent.get(entity);
        BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
        bulletComponent.path.add(new Vector2(bodyComponent.body.getPosition()));
    }

    private void processBulletStar(Entity bullet, Entity star) {
        stopBullet(bullet);
        Mapper.stateComponent.get(star).finish();
    }

    private void processBulletImpenetrableWall(Entity bullet, Entity impenetrableWall) {
        stopBullet(bullet);
    }

    private void processBulletKey(Entity bullet, Entity key) {
        stopBullet(bullet);
        Mapper.stateComponent.get(key).finish();
        if (abstractLevel.isMultiplayer()) {
            WarpController.getInstance().sendGameUpdate(
                MessageCreator.createFinishKeyMessage(
                    Mapper.stateComponent.get(key).getId()
                )
            );
        }
    }

    private void processPlayerStar(Entity player, Entity star) {
        Mapper.stateComponent.get(star).finish();
    }

    private void processPlayerKey(Entity player, Entity key) {
        Mapper.stateComponent.get(key).finish();
        if (abstractLevel.isMultiplayer()) {
            WarpController.getInstance().sendGameUpdate(
                MessageCreator.createFinishKeyMessage(
                    Mapper.stateComponent.get(key).getId()
                )
            );
        }
    }

    private void processBulletDoor(Entity bullet, Entity door) {
        Mapper.doorComponent.get(door).hitDoor();
        stopBullet(bullet);
    }

    private void processBulletTumbler(Entity bullet, Entity tumbler) {
        Mapper.tumblerComponent.get(tumbler).getAction().run();
        stopBullet(bullet);
    }

    private void stopBullet(Entity bullet) {
        BodyComponent bodyComponent = Mapper.bodyComponent.get(bullet);
        bodyComponent.body.setLinearVelocity(0, 0);
        BulletComponent bulletComponent = Mapper.bulletComponent.get(bullet);
        bulletComponent.creationTime = System.currentTimeMillis();
        bulletComponent.lifeTime = 20;
    }

    // Checks whether entity has given type
    private boolean checkType(Entity entity, TypeComponent.Type type) {
        TypeComponent typeComponent = Mapper.typeComponent.get(entity);
        if (typeComponent == null) {
            return false;
        }
        return typeComponent.type.equals(type);
    }

    private void process(Entity entityA, Entity entityB, boolean areSwapped) {
        if (!areSwapped) {
            process(entityB, entityA, true);
        }
        if (checkType(entityA, TypeComponent.Type.BULLET)) {
            processBullet(entityA);
        }
        if (checkType(entityA, TypeComponent.Type.BULLET) &&
            checkType(entityB, TypeComponent.Type.STAR)) {
            processBulletStar(entityA, entityB);
        }
        if (checkType(entityA, TypeComponent.Type.BULLET) &&
                checkType(entityB, TypeComponent.Type.KEY)) {
            processBulletKey(entityA, entityB);
        }
        if (checkType(entityA, TypeComponent.Type.BULLET) &&
            checkType(entityB, TypeComponent.Type.IMPENETRABLE_WALL)) {
            processBulletImpenetrableWall(entityA, entityB);
        }
        if (checkType(entityA, TypeComponent.Type.PLAYER) &&
            checkType(entityB, TypeComponent.Type.STAR)) {
            processPlayerStar(entityA, entityB);
        }
        if (checkType(entityA, TypeComponent.Type.PLAYER) &&
            checkType(entityB, TypeComponent.Type.KEY)) {
            processPlayerKey(entityA, entityB);
        }
        if (checkType(entityA, TypeComponent.Type.BULLET) &&
            checkType(entityB, TypeComponent.Type.DOOR)) {
            processBulletDoor(entityA, entityB);
        }
        if (checkType(entityA, TypeComponent.Type.BULLET) &&
            checkType(entityB, TypeComponent.Type.TUMBLER)) {
            processBulletTumbler(entityA, entityB);
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object aUserData = fixtureA.getBody().getUserData();
        Object bUserData = fixtureB.getBody().getUserData();

        if (!(aUserData instanceof Entity) || !(bUserData instanceof Entity)) {
            return;
        }

        Entity entityA = (Entity) aUserData;
        Entity entityB = (Entity) bUserData;

        process(entityA, entityB, false);
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}