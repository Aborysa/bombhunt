package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.badlogic.gdx.math.Vector2;
import com.bombhunt.game.model.Grid;

@DelayedComponentRemoval
public class NetworkComponent extends Component {
  public String owner = "NONE";
  public boolean isLocal = true;
  public int localTurn = 0;
  public int remoteTurn = 0;
  public int sequenceNumber = 0;

  public boolean autoremove = false;


  public static int playerIdx = 0;
  private static int nextId = 1;

  public static int getNextId(){
    return ((nextId++) % 9000) + 10000 * playerIdx;
  }
}
