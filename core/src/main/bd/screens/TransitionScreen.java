package main.bd.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.bd.BuzzDungeon;

public class TransitionScreen implements Screen {
    private final Screen currentScreen;
    private final Screen nextScreen;

    private final BuzzDungeon game;

    // Once this reaches 1.0f the next scene is shown
    private float alpha = 0;
    // true if fade in, false if fade out
    private boolean fadeDirection = true;

    private float r, g, b;
    private float speed = 0.01f;

    public TransitionScreen setRGB(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        return this;
    }

    public TransitionScreen setDuration(float duration) {
        this.speed = 2 / duration;
        return this;
    }

    public TransitionScreen(Screen current, Screen next, BuzzDungeon game) {
        this.currentScreen = current;
        this.nextScreen = next;

        game.setScreen(next);
        game.setScreen(current);

        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        if (fadeDirection) currentScreen.render(Gdx.graphics.getDeltaTime());
        else nextScreen.render(Gdx.graphics.getDeltaTime());

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.setColor(r, g, b, alpha);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        if (alpha >= 1) {
            fadeDirection = false;
        } else if (alpha <= 0 && !fadeDirection) {
            game.setScreen(nextScreen);
        }
        alpha += fadeDirection ? speed * delta : -speed * delta;
    }

    @Override
    public void resize(int width, int height) {
        if (fadeDirection) currentScreen.resize(width, height);
        else nextScreen.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}