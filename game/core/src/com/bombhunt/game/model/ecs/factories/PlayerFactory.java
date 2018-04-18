package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.services.physic.Collision;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

/**
 * Created by erlin on 23.03.2018.
 */

public class PlayerFactory implements IEntityFactory {
    private World world;
    public Archetype playerArchtype;
    private Grid grid;

    ComponentMapper<TransformComponent> mapTransform;
    ComponentMapper<SpriteComponent> mapSprite;
    //ComponentMapper<VelocityComponent> mapVelocity;
    ComponentMapper<Box2dComponent> mapBox2d;
    ComponentMapper<PlayerComponent> mapPlayerInput;
    ComponentMapper<TimerComponent> mapTimer;


    public int createPlayer(Vector3 pos, Decal sprite) {
        int e = world.create(playerArchtype);

        mapSprite.get(e).sprite = sprite;
        mapTransform.get(e).position.set(pos);
        Body body = Collision.createBody(Collision.dynamicDef, Collision.wallFixture);
        PolygonShape shape = (PolygonShape) body.getFixtureList().get(0).getShape();
        shape.setAsBox((sprite.getWidth() / 2 - 0.2f) * Collision.worldTobox2d, (sprite.getHeight() / 2f - 0.2f) * Collision.worldTobox2d);
        body.setTransform(new Vector2(pos.x, pos.y).scl(Collision.worldTobox2d), 0);

        // prevents the player from rotating about when it collides with other objects.
        body.setFixedRotation(true);
        mapBox2d.get(e).body = body;


        return e;
    }

    @Override
    public int createFromTile(TiledMapTileLayer.Cell cell, TiledMapTileLayer layer, int x, int y, int depth) {
        TiledMapTile tile = cell.getTile();
        TextureRegion tex = tile.getTextureRegion();
        float rotation = 90 * cell.getRotation();

        Decal decal = Decal.newDecal(tex, true);
        Vector3 pos = new Vector3(layer.getTileWidth() * x, layer.getTileHeight() * y, depth).add(new Vector3(layer.getTileWidth() / 2f, layer.getTileHeight() / 2f, 0));
        int e = createPlayer(pos, decal);

        mapTransform.get(e).rotation = rotation;


        return e;
    }

    /*
      public int createFromTile(Cell cell, TiledMapTileLayer layer, int x, int y, int depth){
    TiledMapTile tile = cell.getTile();
    TextureRegion tex = tile.getTextureRegion();
    float rotation = 90*cell.getRotation();

    //
    Decal decal = Decal.newDecal(tex, true);


    Vector3 pos = new Vector3(layer.getTileWidth() * x, layer.getTileHeight() * y, depth).add(new Vector3(layer.getTileWidth()/2f, layer.getTileHeight()/2f, 0));
    int e = createWall(pos, decal, 1);

    mapTransform.get(e).rotation = rotation;

    MapProperties props = layer.getProperties();
    Vector2 veloc = new Vector2(props.get("velocity", 0.0f, Float.class),0f);

    //NOTE: box2d has a hardcoded max speed of 120 units per second
    mapBox2d.get(e).body.setLinearVelocity(veloc);


    return e;
     */

    public void setWorld(World world) {
        this.world = world;

        mapTransform = world.getMapper(TransformComponent.class);
        mapSprite = world.getMapper(SpriteComponent.class);
        //mapVelocity = world.getMapper(VelocityComponent.class);
        mapBox2d = world.getMapper(Box2dComponent.class);
        mapPlayerInput = world.getMapper(PlayerComponent.class);
        mapTimer = world.getMapper(TimerComponent.class);


        playerArchtype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                //.add(VelocityComponent.class)
                .add(Box2dComponent.class)
                .add(PlayerComponent.class)
                .add(TimerComponent.class)
                .build(world);


    }

    @Override
    public void setGrid(Grid grid) {
        this.grid = grid;
    }
}
