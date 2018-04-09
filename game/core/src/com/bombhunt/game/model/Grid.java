package com.bombhunt.game.model;

import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;

public class Grid {

  private int cellSize;
  private int width, height;

  private IntBag[] gridCells;


  public Grid(int width, int height, int cellSize) {
    this.cellSize = cellSize;
    this.width = width;
    this.height = height;

    this.gridCells = new IntBag[width * height];
    for(int i = 0; i < this.gridCells.length; i++) {
      this.gridCells[i] = new IntBag(10);
    }
  }


  public Vector2 getSnappedPosition(Vector2 position) {
    Vector2 snapped = position.cpy();
    snapped.x = Math.floorDiv((int)snapped.x, cellSize);
    snapped.y = Math.floorDiv((int)snapped.y, cellSize);
    snapped.scl(cellSize);
    return snapped;
  }

  public boolean isOutOfBounds(int x, int y){
    return x < 0 || x >= width || y < 0 || y >= height;
  }

  public boolean isOutOfBounds(int index){
    return index < 0 || index >= gridCells.length;
  }

  public int getCellIndex(int x, int y){
    if(isOutOfBounds(x, y)) {
      return -1;
    }
    return x + y * width;
  }

  public int getCellIndex(Vector2 position){
    return getCellIndex(Math.floorDiv((int)position.x, cellSize),Math.floorDiv((int)position.y, cellSize));
  }

  public IntBag getEntities(int index) {
    try{
      return this.gridCells[index];
    }catch(IndexOutOfBoundsException e) {
      return null;
    }
  }

  public IntBag getEntities(int x, int y) {
    return getEntities(getCellIndex(x, y));
  }

  public void addEntity(int e, int index){
    getEntities(index).add(e);
  }

  public void removeEntity(int e, int index){
    getEntities(index).removeValue(e);
  }

  public int getCellSize(){
    return cellSize;
  }
 }