package main.bd.screens;

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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import main.bd.BuzzDungeon;
import main.bd.entities.GamePlayer;
import main.bd.res.Fonts;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GameScreen implements Screen, InputProcessor {

    final BuzzDungeon game;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final Box2DDebugRenderer debugRenderer;
    private final Stage stage;
    private final Stage ui;
    private final InputMultiplexer input;
    public final RayHandler rayHandler;
    public final GamePlayer player;
    private final TiledMap tiledMap;
    private final Label l;

    public World world;
    Music rainMusic;
    public OrthographicCamera camera;
    public Vector2 target = new Vector2();

    public ArrayList<String> messages = new ArrayList<String>();

    public GameScreen(BuzzDungeon game) {
        this.game = game;
        input = new InputMultiplexer();
        stage = new Stage();
        ui = new Stage();
        world = new World(new Vector2(0, 0), true);
        camera = new OrthographicCamera();
        camera.zoom = 1 / 64f;
        stage.getViewport().setCamera(camera);

//        world.setContactListener(new WorldController(this));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("music/dungeon.mp3"));
        rainMusic.setLooping(true);

        rayHandler = new RayHandler(world);
        rayHandler.resizeFBO(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 5);
        rayHandler.setBlur(true);
        RayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(new Color(0.4f, 0.4f, 0.4f, 0.1f));

        tiledMap = new TmxMapLoader().load("maps/maze.tmx");
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("sprites/dungeon.atlas"));

//        HashMap<String, Body> bodies = MapBodyBuilder.generateMaze(tiledMap, 16, world);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 16f);
        debugRenderer = new Box2DDebugRenderer();
        Gdx.input.setInputProcessor(input);

        player = new GamePlayer(textureAtlas, this);
        stage.addActor(player);
        StringBuilder f = new StringBuilder();
        for (String s : messages) f.append(s).append('\n');
        l = new Label(f.toString(), new Label.LabelStyle(Fonts.font, Color.WHITE));
        l.setFontScale(1.5f);
        l.setPosition(100, 60);
        l.setAlignment(Align.bottomLeft);
        ui.addActor(l);

        input.addProcessor(this);
        input.addProcessor(player.controller);
    }

    @Override
    public void show() {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://www.example.com");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", "John"));
        params.add(new BasicNameValuePair("password", "pass"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        client.close();
    }

    float et = 0;

    public void update(float delta) {
        et += delta;
        world.step(delta, 6, 2);

        Vector3 unp = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        target.set(unp.x, unp.y);

        if (messages.size() <= i && et > (i + 1) && i < msgs.length) {
            messages.add(msgs[i]);
            l.setText(messages.stream().collect(Collectors.joining("\n")));
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
        return false;
    }

}
