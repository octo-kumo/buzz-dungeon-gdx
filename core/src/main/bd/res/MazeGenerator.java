package main.bd.res;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import main.bd.screens.MazeScreen;

import java.util.Arrays;
import java.util.Collections;

public class MazeGenerator {
    private final int x;
    private final int y;
    private MazeScreen mazeScreen;
    private final MapLayer props;
    private final TiledMapTileLayer layer;
    private final TiledMapTileLayer floor;
    private final int[][] maze;
    private final TiledMapTile spike;

    public MazeGenerator(int x, int y, MazeScreen mazeScreen) {
        this.x = x;
        this.y = y;
        this.mazeScreen = mazeScreen;
        maze = new int[this.x][this.y];

        props = mazeScreen.tiledMap.getLayers().get("props");
        layer = (TiledMapTileLayer) mazeScreen.tiledMap.getLayers().get("maze");
        floor = (TiledMapTileLayer) mazeScreen.tiledMap.getLayers().get("floor_props");
        spike = mazeScreen.tiledMap.getTileSets().getTile(82);

        final PolygonShape sb = new PolygonShape();
        sb.setAsBox(0.3f, 0.3f, new Vector2(x - .5f, y - .5f), 0);
        mazeScreen.ending.createFixture(new FixtureDef() {{
            shape = sb;
            isSensor = true;
        }});

        generateMaze(0, 0);
        display();
    }

    public void display() {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        Body body = mazeScreen.world.createBody(bd);
        TiledMapTileSets tileSets = mazeScreen.tiledMap.getTileSets();
        TiledMapTile bottom = tileSets.getTile(75);
        TiledMapTile left = tileSets.getTile(58);
        TiledMapTile left_bottom = tileSets.getTile(66);
        TiledMapTile start = tileSets.getTile(89);
        TiledMapTile end = tileSets.getTile(83);
        floor.setCell(0, 0, new TiledMapTileLayer.Cell().setTile(start));
        floor.setCell(x - 1, y - 1, new TiledMapTileLayer.Cell().setTile(end));
        for (int i = 0; i < y; i++) {
            // draw the north edge
            for (int j = 0; j < x; j++) {
                boolean bot = false;
                if ((maze[j][i] & 1) == 0) {
                    bot = true;
                    layer.setCell(j, i, new TiledMapTileLayer.Cell().setTile(bottom));
                    EdgeShape shape = new EdgeShape();
                    shape.set(j, i, j + 1, i);
                    FixtureDef fd = new FixtureDef();
                    fd.shape = shape;
                    fd.density = 1;

                    body.createFixture(fd);
                    shape.dispose();
                }
                if ((maze[j][i] & 8) == 0) {
                    layer.setCell(j, i, new TiledMapTileLayer.Cell().setTile(bot ? left_bottom : left));
                    EdgeShape shape = new EdgeShape();
                    shape.set(j, i, j, i + 1);
                    FixtureDef fd = new FixtureDef();
                    fd.shape = shape;
                    fd.density = 1;

                    body.createFixture(fd);
                    shape.dispose();
                }
            }
        }

        EdgeShape shape = new EdgeShape();
        shape.set(x, 0, x, y);
        body.createFixture(shape, 1);
        shape.dispose();
        shape = new EdgeShape();
        shape.set(0, y, x, y);
        body.createFixture(shape, 1);
        shape.dispose();
    }

    private void generateMaze(int cx, int cy) {
        DIR[] dirs = DIR.values();
        Collections.shuffle(Arrays.asList(dirs));
        boolean allBlocked = true;
        for (DIR dir : dirs) {
            int nx = cx + dir.dx;
            int ny = cy + dir.dy;
            if (between(nx, x) && between(ny, y) && (maze[nx][ny] == 0)) {
                allBlocked = false;
                maze[cx][cy] |= dir.bit;
                maze[nx][ny] |= dir.opposite.bit;
                generateMaze(nx, ny);
            }
        }
        if (allBlocked && MathUtils.random() < 0.5f && !((cx == x - 1 && cy == y - 1) || (cx == 0 || cy == 0))) {
            floor.setCell(cx, cy, new TiledMapTileLayer.Cell().setTile(spike));
            final PolygonShape box = new PolygonShape();
            box.setAsBox(0.3f, 0.3f, new Vector2(cx + 0.5f, cy + 0.5f), 0);
            new PointLight(mazeScreen.rayHandler, 32, Color.FIREBRICK, 2, cx + .5f, cy + .5f);
            mazeScreen.spikes.createFixture(new FixtureDef() {{
                shape = box;
                isSensor = true;
            }});
        } else {
            if (MathUtils.random() < 0.1f) {
                TextureMapObject object = new TextureMapObject(
                        new TextureRegion(new Texture(Gdx.files.internal("icon.png"))));
//                    mazeScreen.atlas.findRegion("chest_empty_open"));
//                object.setX(cx);
//                object.setY(cy);
                props.getObjects().add(object);
            }
        }
    }

    private static boolean between(int v, int upper) {
        return (v >= 0) && (v < upper);
    }

    private enum DIR {
        N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
        private final int bit;
        private final int dx;
        private final int dy;
        private DIR opposite;

        // use the static initializer to resolve forward references
        static {
            N.opposite = S;
            S.opposite = N;
            E.opposite = W;
            W.opposite = E;
        }

        private DIR(int bit, int dx, int dy) {
            this.bit = bit;
            this.dx = dx;
            this.dy = dy;
        }
    }

}