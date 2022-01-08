package main.bd.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import main.bd.BuzzDungeon;
import main.bd.res.Fonts;

public class MainMenuScreen implements Screen {

    final BuzzDungeon game;

    OrthographicCamera camera;

    Texture bg;

    float elapsed = 0;

    public MainMenuScreen(final BuzzDungeon game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        bg = new Texture(Gdx.files.internal("bg.jpeg"));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        ScreenUtils.clear(0, 0, 0, 0);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(bg, 0, 0);
        Fonts.font64title.draw(game.batch, "Welcome to BuzzDungeon!", 200, 300);
        Fonts.font32boldoutline.draw(game.batch, "Tap anywhere to begin!", 200, 200);

        game.batch.end();

        if (elapsed < 1) {
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
            game.shapeRenderer.setColor(0, 0, 0, 1 - elapsed);
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            game.shapeRenderer.end();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }

        if (Gdx.input.isTouched() && game.getScreen() == this)
            game.setScreen(new TransitionScreen(this, new GameScreen(game), game).setDuration(1));
    }

    @Override
    public void resize(int width, int height) {

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
