package main.bd.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import main.bd.BuzzDungeon;
import main.bd.BuzzGen;
import main.bd.res.Fonts;

public class InputScreen implements Screen {
    final BuzzDungeon game;
    private final Label l;
    private final String current;
    private final Stage stage;

    Texture bg;

    float elapsed = 0;

    public InputScreen(BuzzDungeon game, String current) {
        this.game = game;
        this.current = current;
        stage = new Stage();
        bg = new Texture(Gdx.files.internal("bg.jpeg"));

        l = new Label(current, new Label.LabelStyle(Fonts.font, Color.WHITE));
        l.setWrap(true);
        l.setWidth(Gdx.graphics.getWidth() - 400);
        l.setFontScale(1.5f);
        l.setPosition(200, 100);
        l.setAlignment(Align.bottomLeft);

        stage.addActor(l);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        ScreenUtils.clear(0, 0, 0, 0);

        stage.act(delta);
        stage.draw();

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
            game.setScreen(new TransitionScreen(this, new MazeScreen(game, current), game).setDuration(1));
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().setScreenSize(width, height);
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
