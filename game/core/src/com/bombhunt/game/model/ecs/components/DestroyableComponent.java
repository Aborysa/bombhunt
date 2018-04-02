package com.bombhunt.game.model.ecs.components;
import com.artemis.Component;


// Indicate an entity is destoryable
public class DestroyableComponent extends Component{
  // Destroyed after one hit
  public int health = 1;

}