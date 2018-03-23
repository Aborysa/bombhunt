
package com.bombhunt.game.utils.level;

import java.util.ArrayList;
import java.util.HashMap;

import com.artemis.Archetype;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import com.bombhunt.game.ecs.factories.IEntityFactory;

// Wrapper for TiledMap
public class Level{


  private TiledMap map;

  private float width;
  private float height;
  /*HashMap<String, Class<?>> metaTypes = new HashMap<String, Class<?>>(){
    put("collidable", Boolean.class);
    put("is_spawn", Boolean.class);
  };*/
  private HashMap<String, List<MapObject>> metaObjects = new HashMap<String, List<MapObject>>(){{
    put("collidable", new ArrayList<MapObject>());
    put("spawnpoint", new ArrayList<MapObject>());
  }};




  // Layers that spawn entities using IEntityFactory
  private List<TiledMapTileLayer> tileEntityLayer;

  // Only graphical layers
  private List<TiledMapTileLayer> tileDecalLayer;

  // Image layers, backgrounds etc
  private List<TiledMapImageLayer> imageLayers;

  // Object layers, spawn points, static collision meshes, etc
  private List<MapLayer> objectLayers;
  

  public Level(TiledMap map){
    this.map = map;
    

    tileEntityLayer = new ArrayList<TiledMapTileLayer>();
    tileDecalLayer = new ArrayList<TiledMapTileLayer>();

    objectLayers = new ArrayList<MapLayer>();
    imageLayers = new ArrayList<TiledMapImageLayer>();

    parseMap();
  }


  private void parseMap(){
    System.out.println("Parsing map");
    MapLayers layers = map.getLayers();
    int depth = -(layers.getCount()*100 + 1000);
    for(MapLayer layer : layers){
      MapProperties props = layer.getProperties();
      // Set depth of layer if not provided
      props.put("depth", props.get("depth", depth, Integer.class));
      
      // TMP: Find the layer type
      if(layer instanceof TiledMapTileLayer){
        TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
        
        width = Math.max(width, tiledLayer.getTileWidth() * tiledLayer.getWidth());
        height = Math.max(width, tiledLayer.getTileHeight() * tiledLayer.getHeight());
        
        if(props.containsKey("entity_factory")){
          tileEntityLayer.add(tiledLayer);
        }else{
          tileDecalLayer.add(tiledLayer);
        }
      }else if(layer instanceof TiledMapImageLayer){
        imageLayers.add((TiledMapImageLayer)layer);
      }else{
        objectLayers.add(layer);
      }

      depth += 100;
    }
    
  }


  // Spawn entities from entity layers, layers with `entity_factory` defined
  public IntBag createEntities(HashMap<String, IEntityFactory> factories){
    IntBag bag = new IntBag(1024);
    for(TiledMapTileLayer entityLayer : tileEntityLayer){
      MapProperties props = entityLayer.getProperties();
      IEntityFactory factory = factories.get(props.get("entity_factory"));
      
      int depth = props.get("depth", Integer.class);
      for(int x = 0; x < entityLayer.getWidth(); x++){
        for(int y = 0; y < entityLayer.getHeight(); y++){
          Cell cell = entityLayer.getCell(x, y);
          if(cell != null){
            int e = factory.createFromTile(cell, entityLayer, x, y, depth);
            bag.add(e);
          }
        }
      }
    
    }
    return bag;
  }

  // Create decals from tiledDecalLayer
  public List<Decal> createDecals(){
    return null;
  }

  

  public Vector2 getDim(){
    return new Vector2(width, height);
  }

  public TiledMap getMap(){
    return map;
  }


}