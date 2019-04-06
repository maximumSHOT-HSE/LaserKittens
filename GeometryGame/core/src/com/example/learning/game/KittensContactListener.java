package com.example.learning.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.BulletComponent;
import com.example.learning.game.gamelogic.components.TypeComponent;

public class KittensContactListener implements ContactListener {

    public KittensContactListener() {

    }

    /*
     * Checks whether data is bullet (iff body and bullet components exists)
     * or not and
     * if yes then current body position will be added
     * to path of bullet
     * */
    private void processBullet(Object data) {
        if (!(data instanceof Entity)) {
            return;
        }
        Entity entity = (Entity) data;
        TypeComponent typeComponent = Mapper.typeComponent.get(entity);
        if (typeComponent == null ||
                typeComponent.type != TypeComponent.Type.BULLET) {
            return;
        }
        BulletComponent bulletComponent = Mapper.bulletComponent.get(entity);
        BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
        bulletComponent.path.add(new Vector2(bodyComponent.body.getPosition()));
    }

    private boolean isBullet(Object data) {
        if (!(data instanceof Entity)) {
            return false;
        }
        Entity entity = (Entity) data;
        TypeComponent typeComponent = Mapper.typeComponent.get(entity);
        return typeComponent != null && typeComponent.type == TypeComponent.Type.BULLET;
    }

    private boolean isStar(Object data) {
        if (!(data instanceof Entity)) {
            return false;
        }
        Entity entity = (Entity) data;
        TypeComponent typeComponent = Mapper.typeComponent.get(entity);
        return typeComponent != null && typeComponent.type == TypeComponent.Type.STAR;
    }

    private void processBulletStar(Object bullet, Object star) {
        if (isStar(bullet) && isBullet(star)) {
            processBulletStar(star, bullet);
        }
        if (!isBullet(bullet) || !isStar(star)) {
            return;
        }
        Entity bulletEntity = (Entity) bullet;
        Entity starEntity = (Entity) star;
        BodyComponent bodyComponent = Mapper.bodyComponent.get(bulletEntity);
        bodyComponent.body.setLinearVelocity(0, 0);
        BulletComponent bulletComponent = Mapper.bulletComponent.get(bulletEntity);
        bulletComponent.creationTime = System.currentTimeMillis();
        bulletComponent.lifeTime = 20;
        Mapper.stateComponent.get(starEntity).finish();
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Object aUserData = fixtureA.getBody().getUserData();
        Object bUserData = fixtureB.getBody().getUserData();

        processBulletStar(aUserData, bUserData);
        processBullet(aUserData);
        processBullet(bUserData);
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
