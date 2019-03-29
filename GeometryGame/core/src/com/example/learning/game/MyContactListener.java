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

public class MyContactListener implements ContactListener {

    public MyContactListener() {

    }

    /*
     * Checks whether data is bullet (iff body and bullet components exists)
     * or not and
     * if yes then current body position will be added
     * to path of bullet
     * */
    private boolean isBullet(Object data) {
        if (!(data instanceof Entity)) {
            return false;
        }
        Entity entity = (Entity) data;
        BulletComponent bulletComponent = Mapper.bulletComponent.get(entity);
        BodyComponent bodyComponent = Mapper.bodyComponent.get(entity);
        if (bulletComponent == null || bodyComponent == null) {
            return false;
        }
        bulletComponent.path.add(new Vector2(bodyComponent.body.getPosition()));
        return true;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        Object oa = fa.getUserData();
        Object ob = fb.getUserData();
//        isBullet(oa);
        isBullet(ob);
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
