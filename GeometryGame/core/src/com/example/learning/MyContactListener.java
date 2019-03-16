package com.example.learning;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.awt.event.ContainerListener;

public class MyContactListener implements ContactListener {

    private Model model;

    public MyContactListener(Model model) {
        this.model = model;
    }

    @Override
    public void beginContact(Contact contact) {
        System.out.println("!!!-C-O-N-T-A-C-T-!!!");
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        System.out.println(fa.getBody().getType() + " has hit " + fb.getBody().getType());
        fa.getBody().applyForceToCenter(new Vector2(-10000, -100000), true);
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
