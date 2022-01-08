package main.bd.screens;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import main.bd.BuzzDungeon;
import main.bd.BuzzGen;
import main.bd.controllers.MazeController;
import main.bd.entities.GamePlayer;
import main.bd.res.Fonts;
import main.bd.res.MazeGenerator;

import java.util.ArrayList;

public class MazeScreen implements Screen, InputProcessor {

    final BuzzDungeon game;
    public TextureAtlas atlas;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final Box2DDebugRenderer debugRenderer;
    private final Stage stage;
    private final Stage ui;
    public final RayHandler rayHandler;
    public final GamePlayer player;
    public final TiledMap tiledMap;
    private final Label l;

    public World world;
    public Music rainMusic;
    public OrthographicCamera camera;
    public Vector2 target = new Vector2();

    public Body spikes;
    public Body ending;

    public ArrayList<String> messages = new ArrayList<String>();
    public ArrayList<String> to_add = new ArrayList<String>();

    public MazeScreen(BuzzDungeon game, String current) {
        this(game, current, false);
    }

    public MazeScreen(BuzzDungeon game, String current, boolean dontAddMessage) {
        this.game = game;
        InputMultiplexer input = new InputMultiplexer();
        stage = new Stage();
        ui = new Stage();
        world = new World(new Vector2(0, 0), true);
        camera = new OrthographicCamera();
        camera.zoom = 1 / 64f;
        stage.getViewport().setCamera(camera);

        world.setContactListener(new MazeController(this));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("music/dungeon.mp3"));
        rainMusic.setLooping(true);
        rainMusic.setVolume(.5f);

        BodyDef st = new BodyDef();
        st.type = BodyDef.BodyType.StaticBody;
        spikes = world.createBody(st);
        ending = world.createBody(st);

        rayHandler = new RayHandler(world);
        rayHandler.resizeFBO(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 5);
        rayHandler.setBlur(true);
        rayHandler.setAmbientLight(new Color(0f, 0f, 0f, 1f));
        new PointLight(rayHandler, 10, Color.GREEN, 2, 15.5f, 16.5f);
        RayHandler.useDiffuseLight(true);

        tiledMap = new TmxMapLoader().load("maps/maze.tmx");
        atlas = new TextureAtlas(Gdx.files.internal("sprites/dungeon.atlas"));
        new MazeGenerator(16, 16, this);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 16f);
        debugRenderer = new Box2DDebugRenderer();
        Gdx.input.setInputProcessor(input);

        player = new GamePlayer(atlas, this);
        stage.addActor(player);
        StringBuilder f = new StringBuilder();
        for (int j = Math.max(0, messages.size() - 5), messagesSize = messages.size(); j < messagesSize; j++) {
            String s = messages.get(j);
            f.append(s).append('\n');
        }
        Label.LabelStyle style = new Label.LabelStyle(Fonts.font, Color.WHITE);
        l = new Label(f.toString(), style);
        l.setFontScale(1.5f);
        l.setPosition(100, 60);
        l.setAlignment(Align.bottomLeft);
        ui.addActor(l);

        input.addProcessor(this);
        input.addProcessor(player.controller);
        to_add.add(current);
        if (!dontAddMessage) {
            to_add.add("\nThere he finds a maze, a dark, and scary maze, he need to avoid certain spikes as well");
            to_add.add("What will he find?");
            to_add.add("");
            to_add.add("According to the legends, that was told by " + BuzzDungeon.name + "'s ancestors, if you can't find a way, look towards the [#0000ff]Northern Star");
            to_add.add("[WHITE]" + BuzzDungeon.name + " is of cause, very sceptical of such claims");
            to_add.add("He believes it to be the mysterious [RED]Eastern powers[WHITE], where the sun shines bright");
            to_add.add("Where could it be?");
        }
    }

    @Override
    public void show() {
        rainMusic.play();
    }

    float et = 0;
    int i = 0;


    public void died() {
        messages.add("");
        messages.add(BuzzDungeon.name + " was injured by a spike, he watched in horror as the maze rearranged itself");
        StringBuilder f = new StringBuilder();
        for (String s : messages) f.append(s).append('\n');
        game.setScreen(new TransitionScreen(this, new MazeScreen(game, f.toString(), true), game).setDuration(1));
    }

    public void update(float delta) {
        et += delta;
        world.step(delta, 6, 2);

        Vector3 unp = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        target.set(unp.x, unp.y);

        if (messages.size() <= i && et > (i + 1) && i < to_add.size()) {
            messages.add(to_add.get(i));
            StringBuilder f = new StringBuilder();
            for (int j = Math.max(0, messages.size() - 5), messagesSize = messages.size(); j < messagesSize; j++) {
                String s = messages.get(j);
                f.append(s).append('\n');
            }
            l.setText(f.toString());
            i++;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0, 0, 0, 1);
        camera.position.set(player.body.getPosition(), 0);
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(new int[]{0, 1, 2, 3});
        rayHandler.setCombinedMatrix(camera);
        stage.act(delta);
        stage.draw();
        tiledMapRenderer.render(new int[]{4});
        rayHandler.updateAndRender();
//        debugRenderer.render(world, camera.combined);
        ui.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        ui.getViewport().setScreenSize(width, height);
    }

    @Override
    public void pause() {
        rainMusic.pause();
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        rainMusic.dispose();
        rayHandler.dispose();
        tiledMapRenderer.dispose();
        debugRenderer.dispose();
        ui.dispose();
        world.dispose();
        stage.dispose();
        atlas.dispose();
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

    public void won() {
        messages.add("");
        messages.add(BuzzDungeon.name + " was slightly annoyed by the maze, but finished it nevertheless.\n");
        StringBuilder f = new StringBuilder();
        for (String s : messages) f.append(s).append('\n');
        game.setScreen(new TransitionScreen(this, new InputScreen(game, f + "\nHe arrives in the world of \"" + BuzzGen.generate() + "\""), game).setDuration(1));
    }
}
