package com.bombhunt.game.ecs.factories;

import com.artemis.ArtemisPlugin;
import com.artemis.World;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public interface IEntityFactory{
  // Create an entity from a tile cell
  public int createFromTile(Cell cell);
  public void setWorld(World world);
}