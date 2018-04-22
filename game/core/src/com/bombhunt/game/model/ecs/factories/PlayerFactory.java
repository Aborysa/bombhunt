package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
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
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
import com.bombhunt.game.model.ecs.components.NetworkComponent;
import com.bombhunt.game.model.ecs.components.LabelComponent;
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
import com.bombhunt.game.model.ecs.components.InputComponent;


/**
 * Created by erlin on 23.03.2018.
 */


public class PlayerFactory implements IEntityFactory, ITileFactory, INetworkFactory{

    ComponentMapper<TransformComponent> mapTransform;
    ComponentMapper<GridPositionComponent> mapGrid;
    ComponentMapper<Box2dComponent> mapBox2d;
    ComponentMapper<SpriteComponent> mapSprite;
    ComponentMapper<AnimationComponent> mapAnimation;
    ComponentMapper<KillableComponent> mapKillable;
    ComponentMapper<LabelComponent> mapLabel;
    ComponentMapper<PlayerComponent> mapPlayer;
    ComponentMapper<TimerComponent> mapTimer;
    ComponentMapper<InputComponent> mapInput;
    

    ComponentMapper<NetworkComponent> mapNetwork;

    private World world;
    public Archetype playerArchtype;
    private Grid grid;
    private TextureRegion region;

    public PlayerFactory() {
        Assets asset_manager = Assets.getInstance();
        region = asset_manager.get("textures/tilemap1.atlas",
                TextureAtlas.class).findRegion("bomb_party_v4");
    }

    @Override
    public int createFromTile(TiledMapTileLayer.Cell cell, TiledMapTileLayer layer, int x, int y, int depth) {
        TiledMapTile tile = cell.getTile();
        TextureRegion tex = tile.getTextureRegion();
        float rotation = 90 * cell.getRotation();
        Decal decal = Decal.newDecal(tex, true);
        Vector3 pos = new Vector3(layer.getTileWidth() * x, layer.getTileHeight() * y,
                depth).add(new Vector3(layer.getTileWidth() / 2f, layer.getTileHeight() / 2f, 0));
        int e = createPlayer(pos, 0);
        mapTransform.get(e).rotation = rotation;
        return e;
    }

    
    public int createPlayer(Vector3 pos, int index) {
        int e = world.create(playerArchtype);
        mapTransform.get(e).position.set(pos);
        mapGrid.get(e).grid = grid;
        // TODO: CLEAN UP duplicated code from player systems
        int frame = mapPlayer.get(e).direction.getFrame();
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(region, 16, frame, 14 + index, 1),
                60);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(mapPlayer.get(e).direction.getFrame(), true);
        // TODO: Clean this and use circleShape
        Body body = Collision.createBody(Collision.dynamicDef, Collision.playerFixture);
        CircleShape shape = (CircleShape) body.getFixtureList().get(0).getShape();
        //shape.setAsBox((sprite.getWidth() / 2 - 0.3f) * Collision.worldTobox2d, (sprite.getHeight() / 2f - 0.3f) * Collision.worldTobox2d);
        body.setTransform(new Vector2(pos.x, pos.y).scl(Collision.worldTobox2d), 0);
        // prevents the player from rotating about when it collides with other objects.
        body.setFixedRotation(true);
        mapBox2d.get(e).body = body;
        mapLabel.get(e).label = "Player " + (index+1);
        mapGrid.get(e).snapToGrid = false;
        mapPlayer.get(e).index = index;
        return e;
    }


    public void setWorld(World world) {
        this.world = world;
        mapTransform = world.getMapper(TransformComponent.class);
        mapGrid = world.getMapper(GridPositionComponent.class);
        mapBox2d = world.getMapper(Box2dComponent.class);
        mapSprite = world.getMapper(SpriteComponent.class);
        mapAnimation = world.getMapper(AnimationComponent.class);
        mapLabel = world.getMapper(LabelComponent.class);
        mapKillable = world.getMapper(KillableComponent.class);
        mapPlayer = world.getMapper(PlayerComponent.class);
        mapTimer = world.getMapper(TimerComponent.class);
        mapNetwork = world.getMapper(NetworkComponent.class);
        mapInput = world.getMapper(InputComponent.class);
        playerArchtype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(GridPositionComponent.class)
                .add(Box2dComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(LabelComponent.class)
                .add(KillableComponent.class)
                .add(PlayerComponent.class)
                .add(TimerComponent.class)
                .add(InputComponent.class)
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



        mapInput.remove(e);

        NetworkComponent netComp = mapNetwork.get(e);
        netComp.sequenceNumber = seq;
        netComp.owner = m.getSender();
        netComp.isLocal = false;
        netComp.autoremove = true;
        
        System.out.println("Creating remote player with seqnum " + mapNetwork.get(e).sequenceNumber);
        return e;
    }

    public Message pushToNetwork(Message m, int e){
        NetworkComponent netComp = mapNetwork.get(e);


        netComp.sequenceNumber = NetworkComponent.getNextId();
        netComp.autoremove = true;
        netComp.updateRate = 4;
        System.out.println("Creating local player with seqnum " + netComp.sequenceNumber);
        netComp.owner = NetworkManager.getInstance().getPlayerService().getLocalID();
        m.getBuffer().putInt(netComp.sequenceNumber);
        m.putVector(mapTransform.get(e).position);

        return m;

    }
}
