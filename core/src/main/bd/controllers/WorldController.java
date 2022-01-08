package main.bd.controllers;

import com.badlogic.gdx.physics.box2d.*;
import main.bd.screens.TutorialScreen;

public class WorldController implements ContactListener {
    private final TutorialScreen screen;

    public WorldController(TutorialScreen gameScreen) {
        this.screen = gameScreen;
    }

    @Override
    public void beginContact(Contact contact) {
        if (check(contact.getFixtureA(), contact.getFixtureB(), screen.door_btn))
            screen.openDoor();
        if(check(contact.getFixtureA(), contact.getFixtureB(), screen.next_level, screen.player.body)) screen.nextLevel();
        if(check(contact.getFixtureA(), contact.getFixtureB(), screen.killers, screen.player.body)) screen.died();

    }

    public boolean check(Fixture a, Fixture b, Body ba) {
        return a.getBody() == ba || b.getBody() == ba;
    }

    public boolean check(Fixture a, Fixture b, Body ba, Body bb) {
        if (a.getBody() == ba && b.getBody() == bb) return true;
        return a.getBody() == bb && b.getBody() == ba;
    }

    @Override
    public void endContact(Contact contact) {
        if (check(contact.getFixtureA(), contact.getFixtureB(), screen.door_btn))
            screen.closeDoor();
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
