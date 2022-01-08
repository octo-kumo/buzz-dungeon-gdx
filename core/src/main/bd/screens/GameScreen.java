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
import com.badlogic.gdx.utils.ScreenUtils;
import main.bd.BuzzDungeon;
import main.bd.controllers.WorldController;
import main.bd.entities.Player;
import main.bd.res.MapBodyBuilder;

import java.util.HashMap;
import java.util.function.Consumer;

public class GameScreen implements Screen, InputProcessor {
    final BuzzDungeon game;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final Box2DDebugRenderer debugRenderer;
    private final Stage stage;
    private final InputMultiplexer input;
    public final RayHandler rayHandler;
    private final Player player;
    private final Body door;
    private final TiledMap tiledMap;

    public World world;
    Music rainMusic;
    public OrthographicCamera camera;
    public Vector2 target = new Vector2();

    public GameScreen(final BuzzDungeon game) {
        this.game = game;

        input = new InputMultiplexer();
        stage = new Stage();
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

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 16f);
        debugRenderer = new Box2DDebugRenderer();
        Gdx.input.setInputProcessor(input);

        player = new Player(textureAtlas, this);
        stage.addActor(player);

        input.addProcessor(this);
        input.addProcessor(player.controller);
        float distance = 2;
        int rays = 64;
        new PointLight(rayHandler, rays, Color.ORANGE, distance, 1.5f, 9.1f);
        new PointLight(rayHandler, rays, Color.ORANGE, distance, 8.5f, 9.1f);
        new PointLight(rayHandler, rays, new Color(0.5f, 0.5f, 1f, 1f), distance, 2.5f, 9.1f);
        new PointLight(rayHandler, rays, new Color(0.5f, 0.5f, 1f, 1f), distance, 7.5f, 9.1f);
    }

    public void update(float delta) {
        stage.act(delta);
        world.step(delta, 6, 2);

        Vector3 unp = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        target.set(unp.x, unp.y);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.position.set(player.body.getPosition(), 0);
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(new int[]{0, 1, 2});
        rayHandler.setCombinedMatrix(camera);
        stage.draw();
        tiledMapRenderer.render(new int[]{3});
        rayHandler.updateAndRender();
        debugRenderer.render(world, camera.combined);
        tiledMapRenderer.render(new int[]{4});
        update(1 / 60f);
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
        if (keycode == Input.Keys.Q) {
            door.getFixtureList().forEach(new Consumer<Fixture>() {
                @Override
                public void accept(Fixture fixture) {
                    Filter data = fixture.getFilterData();
                    data.groupIndex = (short) (data.groupIndex == 0 ? -1 : 0);
                    ((TiledMapTileLayer) tiledMap.getLayers().get("collisions")).getCell(4, 9)
                            .setTile(tiledMap.getTileSets().getTile(data.groupIndex == 0 ? 930 : 929));
                }
            });
            door.setAwake(true);
        }
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
}
