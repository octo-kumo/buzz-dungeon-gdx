package main.bd.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.bd.res.MapBodyBuilder;

public class Ball extends Actor {
    private final Sprite sprite;
    public final Body body;

    public Ball(float x, float y, float r, World world) {
        body = MapBodyBuilder.makeCircle(x, y, r, world);
        sprite = new Sprite(new Texture(Gdx.files.internal("sprites/ball.png")));
        sprite.setSize(r * 2, r * 2);
        sprite.setOriginCenter();
    }

    @Override
    public void act(float dt) {
        sprite.setCenter(body.getPosition().x, body.getPosition().y);
        sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }
}
