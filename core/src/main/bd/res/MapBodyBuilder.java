package main.bd.res;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import main.bd.screens.GameScreen;

import java.util.HashMap;

public class MapBodyBuilder {

    // The pixels per tile. If your tiles are 16x16, this is set to 16f
    private static float ppt = 16f;

    public static HashMap<String, Body> buildShapes(TiledMap map, float pixels, World world) {
        ppt = pixels;
        HashMap<String, Body> mappedObjects = new HashMap<String, Body>();
        MapObjects objects = map.getLayers().get("box2d").getObjects();

        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) continue;

            Shape shape;

            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject) object);
            } else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject) object);
            } else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject) object);
            } else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject) object);
            } else if (object instanceof EllipseMapObject) {
                shape = getEllipse((EllipseMapObject) object);
            } else {
                continue;
            }

            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bd);
            FixtureDef fd = new FixtureDef();
            fd.shape = shape;
            fd.density = 1;
            if (object.getProperties().get("type") == "sensor") fd.isSensor = true;
            body.createFixture(fd);
            if (object.getName() != null) mappedObjects.put(object.getName(), body);

            shape.dispose();
        }
        return mappedObjects;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / ppt,
                (rectangle.y + rectangle.height * 0.5f) / ppt);
        polygon.setAsBox(rectangle.width * 0.5f / ppt,
                rectangle.height * 0.5f / ppt,
                size,
                0.0f);
        return polygon;
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / ppt);
        circleShape.setPosition(new Vector2(circle.x / ppt, circle.y / ppt));
        return circleShape;
    }

    private static PolygonShape getEllipse(EllipseMapObject circleObject) {
        Ellipse circle = circleObject.getEllipse();
        PolygonShape shape = new PolygonShape();

        float a = circle.width / ppt / 2, b = circle.height / ppt / 2;
        float x = circle.x / ppt + a, y = circle.y / ppt + b;

        float segment = (float) (2 * Math.PI / 8);
        Vector2[] vertices = new Vector2[8];
        for (int i = 0; i < 8; i++) {
            vertices[i] = new Vector2(x + a * MathUtils.cos(segment * i), y + b * MathUtils.sin(segment * i));
        }
        shape.set(vertices);
        return shape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            System.out.println(vertices[i]);
            worldVertices[i] = vertices[i] / ppt;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / ppt;
            worldVertices[i].y = vertices[i * 2 + 1] / ppt;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }
}