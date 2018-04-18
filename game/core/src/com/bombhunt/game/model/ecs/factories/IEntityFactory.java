package com.bombhunt.game.model.ecs.factories;

import com.artemis.World;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.bombhunt.game.model.Grid;

public interface IEntityFactory {
    int createFromTile(Cell cell, TiledMapTileLayer layer, int x, int y, int depth);

    void setWorld(World world);

    void setGrid(Grid grid);
}