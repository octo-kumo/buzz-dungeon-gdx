package main.bd.entities;

import box2dLight.ConeLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.bd.controllers.KeyboardController;
import main.bd.screens.GameScreen;

public class GamePlayer extends Actor {
    public final Body body;
    private final float DASH_CD = 0.4f;
    private final ConeLight light;

    private final Animation<TextureAtlas.AtlasRegion> anim;
    private final Animation<TextureAtlas.AtlasRegion> idle;
    public boolean dead = false;

    private Animation<TextureAtlas.AtlasRegion> cur;

    public KeyboardController controller;
    private boolean flipped = false;
    private float lastDash = 0;
    private final GameScreen screen;
    Sprite sprite;
    TextureAtlas textureAtlas;
    private float et = 0;

    public GamePlayer(TextureAtlas atlas, GameScreen screen) {
        super();
        textureAtlas = atlas;
        cur = idle = new Animation<TextureAtlas.AtlasRegion>(0.1f, atlas.findRegions("knight_m_idle"), Animation.PlayMode.LOOP);
        anim = new Animation<TextureAtlas.AtlasRegion>(0.1f, atlas.findRegions("knight_m_run"), Animation.PlayMode.LOOP);
        sprite = new Sprite(anim.getKeyFrame(0));
        sprite.setSize(1, 1.75f);
        this.screen = screen;

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.allowSleep = false;
        bd.fixedRotation = true;
        bd.position.set(4, 4);
        bd.linearDamping = 5f;
        PolygonShape polygon = new PolygonShape();
        polygon.setAsBox(0.3f, 0.3f,
                new Vector2(0, 0), 0.0f);
        body = screen.world.createBody(bd);
        FixtureDef fd = new FixtureDef();
        fd.shape = polygon;
        fd.density = 1;
        fd.filter.groupIndex = -1;
        body.createFixture(fd);
        light = new ConeLight(screen.rayHandler, 100, Color.WHITE, 9, 0, 0, 0, 60);
        light.setContactFilter((short) 1, (short) -1, (short) -1);
        controller = new KeyboardController();
        polygon.dispose();
    }

    @Override
    public void act(float delta) {
        et += delta;
        if (dead) return;
        if (controller.left) body.applyForceToCenter(-10, 0, true);
        if (controller.right) body.applyForceToCenter(10, 0, true);
        if (controller.up) body.applyForceToCenter(0, 10, true);
        if (controller.down) body.applyForceToCenter(0, -10, true);
        if (!controller.up && !controller.right && !controller.left && !controller.down) {
            body.applyForceToCenter(body.getLinearVelocity().scl(-2f), true);
            cur = idle;
        } else cur = anim;
        if (controller.left && !flipped) flipped = true;
        if (controller.right && flipped) flipped = false;

        if (controller.mright) dash();
    }

    public void dash() {
        if (et - lastDash < DASH_CD) return;
        lastDash = et;
        body.applyLinearImpulse(screen.target.sub(body.getPosition()).nor().scl(8), Vector2.Zero, true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.setRegion(cur.getKeyFrame(et));
        sprite.setFlip(flipped, false);
        sprite.setCenterX(body.getPosition().x);
        sprite.setY(body.getPosition().y - 0.5f);
        sprite.draw(batch);
        light.setDirection(screen.target.sub(body.getPosition()).angleDeg());
        light.setPosition(body.getPosition().add(body.getLinearVelocity().scl(0.05f)));
    }
}