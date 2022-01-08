package main.bd.controllers;

import com.badlogic.gdx.physics.box2d.*;
import main.bd.screens.MazeScreen;

public class MazeController implements ContactListener {
    private MazeScreen mazeScreen;

    public MazeController(MazeScreen mazeScreen) {
        this.mazeScreen = mazeScreen;
    }

    @Override
    public void beginContact(Contact contact) {
        if(check(contact.getFixtureA(),contact.getFixtureB(), mazeScreen.spikes,mazeScreen.player.body)){
            mazeScreen.died();
        }
    }

    @Override
    public void endContact(Contact contact) {

    }


    public boolean check(Fixture a, Fixture b, Body ba) {
        return a.getBody() == ba || b.getBody() == ba;
    }

    public boolean check(Fixture a, Fixture b, Body ba, Body bb) {
        if (a.getBody() == ba && b.getBody() == bb) return true;
        return a.getBody() == bb && b.getBody() == ba;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
