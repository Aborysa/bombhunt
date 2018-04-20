package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
import com.bombhunt.game.model.ecs.components.NetworkComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.graphics.SpriteHelper;
import com.bombhunt.game.services.networking.Message;
import com.bombhunt.game.services.networking.NetworkManager;
import com.bombhunt.game.services.physic.Collision;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

/**
 * Created by erlin on 23.03.2018.
 */

public class PlayerFactory implements IEntityFactory, ITileFactory, INetworkFactory {
    private World world;
    public Archetype playerArchtype;
    private Grid grid;

    ComponentMapper<TransformComponent> mapTransform;
    ComponentMapper<SpriteComponent> mapSprite;
    ComponentMapper<Box2dComponent> mapBox2d;
    ComponentMapper<KillableComponent> mapKillable;
    ComponentMapper<GridPositionComponent> mapGrid;
    ComponentMapper<PlayerComponent> mapPlayerInput;
    ComponentMapper<TimerComponent> mapTimer;

    ComponentMapper<NetworkComponent> mapNetwork;


    private TextureRegion region = Assets.getInstance().get("textures/tilemap1.atlas", TextureAtlas.class).findRegion("bomb_party_v4");

    public int createPlayer(Vector3 pos, int index) {
        int e = world.create(playerArchtype);
        mapTransform.get(e).position.set(pos);
        mapGrid.get(e).grid = grid;
        mapSprite.get(e).sprite = Decal.newDecal(SpriteHelper.createSprites(region, 16, 1, 14 + index, 1).get(0), true);
        Body body = Collision.createBody(Collision.dynamicDef, Collision.playerFixture);
        CircleShape shape = (CircleShape) body.getFixtureList().get(0).getShape();
        // TODO: Clean this and use circleShape
        //shape.setAsBox((sprite.getWidth() / 2 - 0.3f) * Collision.worldTobox2d, (sprite.getHeight() / 2f - 0.3f) * Collision.worldTobox2d);
        body.setTransform(new Vector2(pos.x, pos.y).scl(Collision.worldTobox2d), 0);
        // prevents the player from rotating about when it collides with other objects.
        body.setFixedRotation(true);
        mapBox2d.get(e).body = body;
        mapGrid.get(e).snapToGrid = false;
        return e;
    }

    @Override
    public int createFromTile(TiledMapTileLayer.Cell cell, TiledMapTileLayer layer, int x, int y, int depth) {
        TiledMapTile tile = cell.getTile();
        TextureRegion tex = tile.getTextureRegion();
        float rotation = 90 * cell.getRotation();
        /*Decal decal = Decal.newDecal(tex, true);*/
        Vector3 pos = new Vector3(layer.getTileWidth() * x, layer.getTileHeight() * y,
                depth).add(new Vector3(layer.getTileWidth() / 2f, layer.getTileHeight() / 2f, 0));
        int e = createPlayer(pos, 0);
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
        mapBox2d = world.getMapper(Box2dComponent.class);
        mapKillable = world.getMapper(KillableComponent.class);
        mapGrid = world.getMapper(GridPositionComponent.class);
        mapPlayerInput = world.getMapper(PlayerComponent.class);
        mapTimer = world.getMapper(TimerComponent.class);
        mapNetwork = world.getMapper(NetworkComponent.class);

        playerArchtype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                .add(Box2dComponent.class)
                .add(KillableComponent.class)
                .add(GridPositionComponent.class)
                .add(PlayerComponent.class)
                .add(TimerComponent.class)
                .add(NetworkComponent.class)
                .build(world);
    }

    @Override
    public void setGrid(Grid grid) {
        this.grid = grid;
    }


    public int createFromMessage(Message m){
        int seq = m.getBuffer().getInt();
        Vector3 pos = m.getVector3();
        int e = createPlayer(pos, NetworkManager.getInstance().getPlayerInfo(m.getSender()).playerIndex);



        mapPlayerInput.remove(e);

        NetworkComponent netComp = mapNetwork.get(e);
        netComp.sequenceNumber = seq;
        netComp.owner = m.getSender();
        netComp.isLocal = false;
        
        System.out.println("Creating remote player with seqnum " + mapNetwork.get(e).sequenceNumber);
        return e;
    }

    public Message pushToNetwork(Message m, int e){
        NetworkComponent netComp = mapNetwork.get(e);


        netComp.sequenceNumber = NetworkComponent.getNextId();
        System.out.println("Creating local player with seqnum " + netComp.sequenceNumber);
        netComp.owner = NetworkManager.getInstance().getPlayerService().getLocalID();
        m.getBuffer().putInt(netComp.sequenceNumber);
        m.putVector(mapTransform.get(e).position);

        return m;
    }
}
