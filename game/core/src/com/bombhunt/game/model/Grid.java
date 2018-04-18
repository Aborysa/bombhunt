package com.bombhunt.game.model;

import android.support.annotation.NonNull;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Grid {

    private static Grid instance = null;

    private int cellSize;
    private int width, height;

    private World world;

    private IntBag[] gridCells;

    public static Grid getInstance() {
        return instance;
    }

    public Grid(World world, int width, int height, int cellSize) {
        this.cellSize = cellSize;
        this.width = width;
        this.height = height;
        this.world = world;
        this.gridCells = new IntBag[width * height];
        for (int i = 0; i < this.gridCells.length; i++) {
            this.gridCells[i] = new IntBag(10);
        }
        instance = this;
    }

    public Vector2 getSnappedPosition(Vector2 position) {
        Vector2 snapped = position.cpy();
        snapped.x = (int) (snapped.x / cellSize);
        snapped.y = (int) (snapped.y / cellSize);
        snapped.scl(cellSize);
        return snapped;
    }

    private boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }

    public boolean isOutOfBounds(int index) {
        return index < 0 || index >= gridCells.length;
    }

    private int getCellIndex(int x, int y) {
        if (isOutOfBounds(x, y)) {
            return -1;
        }
        return x + y * width;
    }

    public int getCellIndex(Vector2 position) {
        return getCellIndex((int) (position.x / cellSize), (int) (position.y / cellSize));
    }

    public int getCellIndex(Vector3 position) {
        return getCellIndex((int) (position.x / cellSize), (int) (position.y / cellSize));
    }

    public IntBag getEntities(int index) {
        try {
            return this.gridCells[index];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public IntBag getEntities(int x, int y) {
        return getEntities(getCellIndex(x, y));
    }

    public void addEntity(int e, int index) {
        getEntities(index).add(e);
    }

    public void removeEntity(int e, int index) {
        getEntities(index).removeValue(e);
    }

    public int getCellSize() {
        return cellSize;
    }


    public IntBag raycast(Vector2 pos, Vector2 line, Aspect.Builder aspectFilter) {
        return raycast(pos, line, true, aspectFilter);
    }

    public IntBag raycast(Vector2 pos, Vector2 line, boolean includeFirst, Aspect.Builder aspectFilter) {
        return raycast(pos, line, includeFirst, new EntityFilter() {
            @Override
            public boolean filter(int e) {
                return world.getAspectSubscriptionManager().get(aspectFilter).getEntities().contains(e);
            }
        });
    }


    public IntBag raycast(Vector2 pos, Vector2 line, EntityFilter stopFilter) {
        return raycast(pos, line, true, stopFilter);
    }


    public IntBag raycast(Vector2 pos, Vector2 line, boolean includeFirst, EntityFilter stopFilter) {
        pos = getSnappedPosition(pos);
        Vector2 dir = line.cpy().nor();
        int length = (int) Math.ceil(line.len());
        IntBag entities = null;
        for (int i = includeFirst ? 0 : 1; i < length; i++) {
            int cellIdx = getCellIndex(dir.cpy().scl(i).add(pos));
            entities = getEntities(cellIdx);
            if (entities == null) {
                return null;
            }
            for (int j = 0; j < entities.size(); j++) {
                if (stopFilter.filter(entities.get(j))) {
                    return entities;
                }
            }
        }
        return entities;
    }

    public Boolean detect(Vector3 position, ComponentMapper<? extends Component> mapComponent) {
        IntBag entities = getEntities(getCellIndex(position));
        for (int i = 0; i < entities.size(); i++) {
            int e = entities.get(i);
            if (mapComponent.has(e)) {
                return true;
            }
        }
        return false;
    }

    private IntBag filterEntities(Vector3 position, ComponentMapper<? extends Component> mapComponent) {
        int cellIndex = getCellIndex(position);
        IntBag entities = getEntities(cellIndex);
        IntBag matchingEntities = new IntBag();
        for (int e : entities.getData()) {
            if (mapComponent.has(e)) {
                matchingEntities.add(e);
            }
        }
        return matchingEntities;
    }
}