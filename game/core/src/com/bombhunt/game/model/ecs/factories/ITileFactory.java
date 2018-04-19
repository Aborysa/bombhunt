package com.bombhunt.game.model.ecs.factories;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by samuel on 19/04/18.
 */

public interface ITileFactory {
    int createFromTile(TiledMapTileLayer.Cell cell, TiledMapTileLayer layer, int x, int y, int depth);
}
