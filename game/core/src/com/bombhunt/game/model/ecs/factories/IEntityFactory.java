package com.bombhunt.game.model.ecs.factories;

import com.artemis.World;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public interface IEntityFactory{
  // Create an entity from a tile cell
  public int createFromTile(Cell cell, TiledMapTileLayer layer, int x, int y, int depth);
  public void setWorld(World world);
}