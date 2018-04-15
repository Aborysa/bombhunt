package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.bombhunt.game.model.Grid;


public class NetworkComponent extends Component {
  public String owner = "LOCAL";
  public int localTurn = 0;
  public int remoteTurn = 0;
  public int sequenceNumber = 0;


  private static int nextId = 1;
  public static int getNextId(){
    return nextId++;
  }
}
