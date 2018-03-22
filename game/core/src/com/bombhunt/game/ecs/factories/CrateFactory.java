package com.bombhunt.game.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ArtemisPlugin;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.ecs.components.AnimationComponent;
import com.bombhunt.game.ecs.components.DestroyableComponent;
import com.bombhunt.game.ecs.components.SpriteComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.ecs.components.VelocityComponent;

public class CrateFactory{
  

  private static World world;
  public static Archetype crateArchtype; 
  private static ComponentMapper<TransformComponent> mapTransform; 
  private static ComponentMapper<SpriteComponent> mapSprite;
  private static ComponentMapper<DestroyableComponent> mapDestroyable;

  public CrateFactory(){}

  public static void setup(World world) {
    CrateFactory.world = world;
    crateArchtype = new ArchetypeBuilder()
      .add(TransformComponent.class)
      .add(SpriteComponent.class)
      .add(DestroyableComponent.class)
      .build(world);

    mapTransform = world.getMapper(TransformComponent.class);
    mapSprite = world.getMapper(SpriteComponent.class);
    mapDestroyable = world.getMapper(DestroyableComponent.class);
  }

  


  public static int createCrate(Vector3 position, Decal sprite, int health){
    int e = world.create(crateArchtype);

    mapSprite.get(e).sprite = sprite;
    mapTransform.get(e).position.set(position);
    mapDestroyable.get(e).health = health;


    return e;
  }
}