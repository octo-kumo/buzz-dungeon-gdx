package main.bd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.bd.res.Fonts;
import main.bd.screens.GameScreen;

public class BuzzDungeon extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        Fonts.init();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        this.setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        Fonts.dispose();
        batch.dispose();
    }
}
