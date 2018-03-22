
package com.bombhunt.game.utils.level;

import java.util.HashMap;

import com.artemis.Archetype;
import com.artemis.World;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.bombhunt.game.ecs.factories.IEntityFactory;

// Wrapper for TiledMap
public class Level{


  private TiledMap map;
  private World world;

  private float width;
  private float height;
  /*HashMap<String, Class<?>> metaTypes = new HashMap<String, Class<?>>(){
    put("collidable", Boolean.class);
    put("is_spawn", Boolean.class);
  };*/
  private HashMap<String, MapObjects> metaObjects = new HashMap<String, MapObjects>(){{
    put("collidable", new MapObjects());
    put("is_spawn", new MapObjects());
  }};

  // Map of entity type and factory
  private HashMap<String, IEntityFactory> factories;

  public Level(TiledMap map, World world, HashMap<String, IEntityFactory> factories){
    this.map = map;
    this.world = world;
    this.factories = factories;
  }


  public void create(){
    System.out.println("Parsing map");
    MapLayers layers = map.getLayers();
    int depth = -(layers.getCount()*100 + 100);
    for(MapLayer layer : layers){
      depth += 100;
      boolean isTileLayer = layer instanceof TiledMapTileLayer;
      MapObjects objects = layer.getObjects();
      // Find all objects, spawn points and collidable walls
      for(MapObject object : objects){
        MapProperties props = object.getProperties();
        for(String key : metaObjects.keySet()){
          if(props.get(key, false, Boolean.class)){
            metaObjects.get(key).add(object);
          }
        }
      }
      if(isTileLayer){
        TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
        width = Math.max(width, tiledLayer.getTileWidth() * tiledLayer.getWidth());
        height = Math.max(width, tiledLayer.getTileHeight() * tiledLayer.getHeight());
        MapProperties props = tiledLayer.getProperties();
        /* what type of tiles is in this layer? */

        // This layer should spawn entities
        if(props.containsKey("entity_factory")){
          IEntityFactory factory = factories.get (props.get("entity_factory"));
          System.out.println(tiledLayer.getWidth());
          System.out.println(tiledLayer.getHeight());

          for(int x=0; x < tiledLayer.getWidth(); x++){
            for(int y=0; y < tiledLayer.getWidth(); y++){
              Cell cell = tiledLayer.getCell(x, y);
              if(cell != null) {
                System.out.println("Creating entities");
                int e = factory.createFromTile(cell, tiledLayer, x, y, depth);
              }

            }
          }

          //world.get;
        }
      }
    }
  }

  public Vector2 getDim(){
    return new Vector2(width, height);
  }


}