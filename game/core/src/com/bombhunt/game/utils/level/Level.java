
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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;

// Wrapper for TiledMap
public class Level{


  private TiledMap map;
  private World world;

  /*HashMap<String, Class<?>> metaTypes = new HashMap<String, Class<?>>(){
    put("collidable", Boolean.class);
    put("is_spawn", Boolean.class);
  };*/
  HashMap<String, MapObjects> metaObjects = new HashMap<String, MapObjects>(){{
    put("collidable", new MapObjects());
    put("is_spawn", new MapObjects());
  }};

  // Map of entity type and factory
  // TODO: move to json file
  
  HashMap<String, Archetype> metaTiles = new HashMap<String,Archetype>(){{
    put("crate", null);
  }};

  public Level(TiledMap map, World world){
    this.map = map;
    this.world = world;

    parseMap();
  }


  private void parseMap(){
    System.out.println("Parsing map");
    MapLayers layers = map.getLayers();
    for(MapLayer layer : layers){
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
        MapProperties props = tiledLayer.getProperties();
        /* what type of tiles is in this layer? */
        tiledLayer.getCell(0, 0).getTile();
        // This layer should spawn entities
        if(props.containsKey("entity_factory")){
          //Class factory = Class.forName(props.get("entity_factory"));
          //world.get;
        }
      }


    }



  }
}