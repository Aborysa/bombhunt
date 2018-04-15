
package com.bombhunt.game.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.bombhunt.game.services.graphics.SpriteHelper;
import com.bombhunt.game.services.physic.Collision;
import com.bombhunt.game.model.ecs.factories.IEntityFactory;

// Wrapper for TiledMap
public class Level {

    private TiledMap map;

    private float width;
    private float height;
    /*HashMap<String, Class<?>> metaTypes = new HashMap<String, Class<?>>(){
      put("collidable", Boolean.class);
      put("is_spawn", Boolean.class);
    };*/
    private HashMap<String, List<MapObject>> metaObjects = new HashMap<String, List<MapObject>>() {{
        put("collidable", new ArrayList<MapObject>());
        put("spawnpoint", new ArrayList<MapObject>());
    }};

    // Layers that spawn entities using IEntityFactory
    private List<TiledMapTileLayer> tileEntityLayers;

    // Only graphical layers
    private List<TiledMapTileLayer> tileDecalLayers;

    // Image layers, backgrounds etc
    private List<TiledMapImageLayer> imageLayers;

    // Object layers, spawn points, static collision meshes, etc
    private List<MapLayer> objectLayers;

    private EarClippingTriangulator triangulator = new EarClippingTriangulator();

    public Level(TiledMap map) {
        this.map = map;
        tileEntityLayers = new ArrayList<>();
        tileDecalLayers = new ArrayList<>();
        objectLayers = new ArrayList<>();
        imageLayers = new ArrayList<>();
        parseMap();
    }

    private void parseMap() {
        System.out.println("Parsing map");
        MapLayers layers = map.getLayers();

        int depth = -(layers.getCount() * 100 + 1000);
        for (MapLayer layer : layers) {
            MapProperties props = layer.getProperties();
            // Set depth of layer if not provided
            props.put("depth", props.get("depth", depth, Integer.class));

            // TMP: Find the layer type
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;

                width = Math.max(width, tiledLayer.getTileWidth() * tiledLayer.getWidth());
                height = Math.max(width, tiledLayer.getTileHeight() * tiledLayer.getHeight());

                if (props.containsKey("entity_factory")) {
                    tileEntityLayers.add(tiledLayer);
                } else {
                    tileDecalLayers.add(tiledLayer);
                }
            } else if (layer instanceof TiledMapImageLayer) {
                imageLayers.add((TiledMapImageLayer) layer);
            } else {
                objectLayers.add(layer);

                for (MapObject object : layer.getObjects()) {
                    MapProperties objectProps = object.getProperties();

          /*
            NOTE: May want to use a 'type' prop to identify what type the object is.
            i.e `type: collidable` instead of `collidable: true`
          */
                    for (String prop : metaObjects.keySet()) {
                        if (objectProps.get(prop, false, Boolean.class)) {
                            metaObjects.get(prop).add(object);
                        }
                    }
                }
            }

            depth += 100;
        }

    }

    // Spawn entities from entity layers, layers with `entity_factory` defined
    public IntBag createEntities(HashMap<String, IEntityFactory> factories) {
        IntBag bag = new IntBag(1024);
        for (TiledMapTileLayer entityLayer : tileEntityLayers) {
            MapProperties props = entityLayer.getProperties();
            IEntityFactory factory = factories.get(props.get("entity_factory"));
            int depth = props.get("depth", Integer.class);
            for (int x = 0; x < entityLayer.getWidth(); x++) {
                for (int y = 0; y < entityLayer.getHeight(); y++) {
                    Cell cell = entityLayer.getCell(x, y);
                    if (cell != null) {
                        int e = factory.createFromTile(cell, entityLayer, x, y, depth);
                        bag.add(e);
                    }
                }
            }

        }
        return bag;
    }

    public Grid createGrid(World world) {
        return new Grid(world, 50, 50, 16);
    }

    // Create decals from tiledDecalLayers
    public List<Decal> createDecals() {
        List<Decal> decals = new ArrayList<>();
        for(TiledMapTileLayer decalLayer : tileDecalLayers){
            int depth = decalLayer.getProperties().get("depth", Integer.class);
            for (int x = 0; x < decalLayer.getWidth(); x++) {
                for (int y = 0; y < decalLayer.getHeight(); y++) {
                    Cell cell = decalLayer.getCell(x, y);
                    if (cell != null) {
                        Decal decal = Decal.newDecal(cell.getTile().getTextureRegion(), true);
                        Vector3 pos = new Vector3(decalLayer.getTileWidth() * x, decalLayer.getTileHeight() * y, depth).add(new Vector3(decalLayer.getTileWidth()/2f, decalLayer.getTileHeight()/2f, 0));
                        decal.setPosition(pos );
                        decals.add(decal);
                    }
                }
            }
        }
        return decals;
    }


    private Body createBodyFromPolygon(com.badlogic.gdx.physics.box2d.World box2d, Polygon poly) {

        Vector2 pos = poly.getBoundingRectangle().getPosition(new Vector2());
        float[] verticies = poly.getTransformedVertices();

        // Translate vertecies to box2d coords
        for (int i = 0; i < verticies.length; i++) {
            // X-coord
            if (i % 2 == 0) {
                verticies[i] -= pos.x;
            }
            // Y-coord
            else {
                verticies[i] -= pos.y;
            }
            verticies[i] *= Collision.worldTobox2d;
        }
        // Box2d does not support convex shapes so triangulate the polygon,
        // TODO: add check to see if the shape is convex first
        short[] indecies = triangulator.computeTriangles(verticies).toArray();

        // The triangle count
        int tcount = indecies.length / 3;

        // Fixture defs, one for each triangle
        FixtureDef[] fixtures = new FixtureDef[tcount];
        Arrays.fill(fixtures, Collision.wallFixture);
        // Create the body
        Body box2dBody = Collision.createBody(box2d, Collision.saticDef, fixtures);
        int fixIdx = 0;
        for (int i = 0; i < indecies.length; i += 3) {
            int idx1 = indecies[i] * 2;
            int idx2 = indecies[i + 1] * 2;
            int idx3 = indecies[i + 2] * 2;

            float[] triangle = {
                    verticies[idx1], verticies[idx1 + 1],
                    verticies[idx2], verticies[idx2 + 1],
                    verticies[idx3], verticies[idx3 + 1]
            };
            PolygonShape shape = (PolygonShape) box2dBody.getFixtureList().get(fixIdx).getShape();
            shape.set(triangle);
            fixIdx++;
        }
        // Move the body to the correct position
        box2dBody.setTransform(pos.scl(Collision.worldTobox2d), 0);
        return box2dBody;
    }

    // Creates a list of collision bodies from the maps objects
    public List<Body> createCollisionBodies(com.badlogic.gdx.physics.box2d.World box2d) {
        ArrayList<Body> bodies = new ArrayList<Body>(metaObjects.get("collidable").size());
        System.out.println(String.format("createCollisionBodies %d", metaObjects.get("collidable").size()));
        for (MapObject object : metaObjects.get("collidable")) {
            Body box2dBody = null;
            // Find out what the shape type is
            if (object instanceof RectangleMapObject) {
                box2dBody = Collision.createBody(box2d, Collision.saticDef, Collision.wallFixture);
                PolygonShape shape = (PolygonShape) box2dBody.getFixtureList().get(0).getShape();

                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                Vector2 dim = rect.getSize(new Vector2()).scl(0.5f).scl(Collision.worldTobox2d);
                Vector2 pos = rect.getPosition(new Vector2()).scl(Collision.worldTobox2d);

                shape.setAsBox(dim.x, dim.y, dim, 0f);

                box2dBody.setTransform(pos, 0);
            } else if (object instanceof PolygonMapObject) {
                Polygon poly = ((PolygonMapObject) object).getPolygon();
                box2dBody = createBodyFromPolygon(box2d, poly);
            }

            bodies.add(box2dBody);
        }
        return bodies;
    }

    public Vector2 getDim() {
        return new Vector2(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

}