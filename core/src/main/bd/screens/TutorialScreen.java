package main.bd.screens;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import main.bd.BuzzDungeon;
import main.bd.BuzzGen;
import main.bd.controllers.WorldController;
import main.bd.entities.Ball;
import main.bd.entities.Player;
import main.bd.res.Fonts;
import main.bd.res.MapBodyBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TutorialScreen implements Screen, InputProcessor {
    final BuzzDungeon game;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final Box2DDebugRenderer debugRenderer;
    private final Stage stage;
    private final Stage ui;
    private final InputMultiplexer input;
    public final RayHandler rayHandler;
    public final Player player;
    private final Body door;
    private final TiledMap tiledMap;
    private final Label l;

    public final Body door_btn;
    public final Body next_level;
    public final Ball ball;
    public final Body killers;

    public World world;
    Music rainMusic;
    public OrthographicCamera camera;
    public Vector2 target = new Vector2();

    public ArrayList<String> messages = new ArrayList<String>();

    public TutorialScreen(final BuzzDungeon game) {
        this.game = game;

        input = new InputMultiplexer();
        stage = new Stage();
        ui = new Stage();
        world = new World(new Vector2(0, 0), true);
        camera = new OrthographicCamera();
        camera.zoom = 1 / 64f;
        stage.getViewport().setCamera(camera);

        world.setContactListener(new WorldController(this));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("music/dungeon.mp3"));
        rainMusic.setLooping(true);

        rayHandler = new RayHandler(world);
        rayHandler.resizeFBO(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 5);
        rayHandler.setBlur(true);
        RayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(new Color(0.4f, 0.4f, 0.4f, 0.1f));

        tiledMap = new TmxMapLoader().load("maps/tutorial.tmx");
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("sprites/dungeon.atlas"));

        HashMap<String, Body> bodies = MapBodyBuilder.buildShapes(tiledMap, 16, world);
        door = bodies.get("door");
        door_btn = bodies.get("door_button");
        next_level = bodies.get("next_level");
        killers = bodies.get("killers");

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 16f);
        debugRenderer = new Box2DDebugRenderer();
        Gdx.input.setInputProcessor(input);

        player = new Player(textureAtlas, this);
        stage.addActor(player);
        stage.addActor(ball = new Ball(3, 3, 0.8f, world));

        l = new Label(messages.stream().collect(Collectors.joining("\n")), new Label.LabelStyle(Fonts.font, Color.WHITE));
        l.setFontScale(1.5f);
        l.setPosition(100, 60);
        l.setAlignment(Align.bottomLeft);
        ui.addActor(l);

        input.addProcessor(this);
        input.addProcessor(player.controller);
        float distance = 2;
        int rays = 64;
        new PointLight(rayHandler, rays, Color.ORANGE, distance, 1.5f, 9.1f);
        new PointLight(rayHandler, rays, Color.ORANGE, distance, 8.5f, 9.1f);
        new PointLight(rayHandler, rays, new Color(0.5f, 0.5f, 1f, 1f), distance, 2.5f, 9.1f);
        new PointLight(rayHandler, rays, new Color(0.5f, 0.5f, 1f, 1f), distance, 7.5f, 9.1f);
    }

    float et = 0;

    String[] msgs = {
            "Jack arrives in a everchanging dungeon made up of buzzwords, he is a knight who loves adventures.",
            "He will escape this place, to continue playing the new game that he just bought for $100",
            "... and to see his girlfriend again (the princess)",
            "",
            "but how, the dungeon has changed form for over 10 times already",
            "each time he enters it, it looks different",
            "",
            "No matter, Jack will try again..."
    };
    int i = 0;

    public void update(float delta) {
        et += delta;
        world.step(delta, 6, 2);

        Vector3 unp = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        target.set(unp.x, unp.y);

        if (messages.size() <= i && et > (i + 1) && i < msgs.length) {
            messages.add(msgs[i]);
            StringBuilder f = new StringBuilder();
            for (String s : messages) f.append(s).append('\n');
            l.setText(f.toString());
            i++;
        }
    }

    @Override
    public void render(float delta) {
        update(1 / 60f);
        ScreenUtils.clear(0, 0, 0, 1);
        camera.position.set(player.body.getPosition(), 0);
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(new int[]{0, 1, 2});
        rayHandler.setCombinedMatrix(camera);
        stage.act(delta);
        stage.draw();
        tiledMapRenderer.render(new int[]{3});
        rayHandler.updateAndRender();
        debugRenderer.render(world, camera.combined);
        tiledMapRenderer.render(new int[]{4});
        ui.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
    }

    @Override
    public void show() {
        rainMusic.play();
    }

    @Override
    public void hide() {
        rainMusic.pause();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        rainMusic.dispose();
        rayHandler.dispose();
        tiledMapRenderer.dispose();
        world.dispose();
        stage.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.zoom = camera.zoom * (1 + amountY * 0.1f);
        camera.zoom = Math.max(Math.min(camera.zoom, 1 / 50f), 1 / 120f);
        return false;
    }

    public void openDoor() {
        door.getFixtureList().forEach(new Consumer<Fixture>() {
            @Override
            public void accept(Fixture fixture) {
                Filter data = fixture.getFilterData();
                data.groupIndex = -1;
                ((TiledMapTileLayer) tiledMap.getLayers().get("collisions")).getCell(4, 9)
                        .setTile(tiledMap.getTileSets().getTile(data.groupIndex == 0 ? 930 : 929));
            }
        });
    }

    public void closeDoor() {
        door.getFixtureList().forEach(new Consumer<Fixture>() {
            @Override
            public void accept(Fixture fixture) {
                Filter data = fixture.getFilterData();
                data.groupIndex = 0;
                ((TiledMapTileLayer) tiledMap.getLayers().get("collisions")).getCell(4, 9)
                        .setTile(tiledMap.getTileSets().getTile(data.groupIndex == 0 ? 930 : 929));
            }
        });
    }

    public void nextLevel() {
        if (game.getScreen() == this) {
            game.setScreen(new TransitionScreen(this, new InputScreen(game,
                    "Below is a long story of Jack, the knight.\n" +
                            "\n" +
                            "Jack arrives in a everchanging dungeon, made up of buzzwords, he is a knight who loves adventures. He is trying to escape this dungeon, but it morphs around him, using popular buzzwords as the seed.\n" +
                            "\n" +
                            "He arrives in the world of " + BuzzGen.generate() + "\n"), game).setDuration(0.5f));
        }
    }

    public void died() {
        player.dead = true;
        game.setScreen(new TransitionScreen(this, new TutorialScreen(game), game).setDuration(0.5f));
    }
}
