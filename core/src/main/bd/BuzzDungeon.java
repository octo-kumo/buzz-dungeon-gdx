package main.bd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import main.bd.res.Fonts;
import main.bd.screens.InputScreen;

public class BuzzDungeon extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        Fonts.init();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
//        this.setScreen(new TutorialScreen(this));
        this.setScreen(new InputScreen(this, "Below is a long story of Jack, the knight.\n" + "\n" + "Jack arrives in a everchanging dungeon, made up of buzzwords, he is a knight who loves adventures. He is trying to escape this dungeon, but it morphs around him, using popular buzzwords as the seed.\n"));
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
