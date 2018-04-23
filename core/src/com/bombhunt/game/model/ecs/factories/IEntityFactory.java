package com.bombhunt.game.model.ecs.factories;

import com.artemis.World;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.bombhunt.game.model.Grid;

public interface IEntityFactory {

    void setWorld(World world);

    void setGrid(Grid grid);
}