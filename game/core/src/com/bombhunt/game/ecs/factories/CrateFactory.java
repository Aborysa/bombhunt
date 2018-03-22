package com.bombhunt.game.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ArtemisPlugin;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.annotations.Wire;
import com.artemis.injection.FieldResolver;
import com.artemis.injection.WiredFieldResolver;
import com.artemis.utils.reflect.Field;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.ecs.components.AnimationComponent;
import com.bombhunt.game.ecs.components.DestroyableComponent;
import com.bombhunt.game.ecs.components.SpriteComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.ecs.components.VelocityComponent;
import com.bombhunt.game.ecs.systems.SpriteSystem;

public class CrateFactory implements IEntityFactory {
  

  private World world;
  public Archetype crateArchtype; 
  

  private ComponentMapper<TransformComponent> mapTransform; 
  private ComponentMapper<SpriteComponent> mapSprite;
  private ComponentMapper<DestroyableComponent> mapDestroyable;

 
  public int createFromTile(Cell cell, TiledMapTileLayer layer, int x, int y, int depth){
    TiledMapTile tile = cell.getTile();
    TextureRegion tex = tile.getTextureRegion();
    float rotation = 90*cell.getRotation();

    //
    Decal decal = Decal.newDecal(tex, true);


    Vector3 pos = new Vector3(layer.getTileWidth() * x, layer.getTileHeight() * y, depth);
    int e = createCrate(pos, decal, 1);

    mapTransform.get(e).rotation = rotation;

    return e;
  }


  public void setWorld(World world){
    this.world = world;
    mapTransform = world.getMapper(TransformComponent.class);
    mapSprite = world.getMapper(SpriteComponent.class);
    mapDestroyable = world.getMapper(DestroyableComponent.class);
    crateArchtype = new ArchetypeBuilder()
      .add(TransformComponent.class)
      .add(SpriteComponent.class)
      .add(DestroyableComponent.class)
      .build(world);
    
    
  }

  public int createCrate(Vector3 position, Decal sprite, int health){
    int e = world.create(crateArchtype);

    mapSprite.get(e).sprite = sprite;
    mapTransform.get(e).position.set(position);
    mapDestroyable.get(e).health = health;


    return e;
  }
}