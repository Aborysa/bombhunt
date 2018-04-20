package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
import com.bombhunt.game.model.ecs.components.LabelComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.graphics.SpriteHelper;
import com.bombhunt.game.services.physic.Collision;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

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
        int e = createPlayer(pos, decal);
        mapTransform.get(e).rotation = rotation;
        return e;
    }

    public int createPlayer(Vector3 pos, Decal sprite) {
        int e = world.create(playerArchtype);
        mapTransform.get(e).position.set(pos);
        mapGrid.get(e).grid = grid;
        // TODO: CLEAN UP duplicated code from player systems
        int frame = mapPlayer.get(e).direction.getFrame();
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(region, 16, frame, 17, 1),
                60);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(mapPlayer.get(e).direction.getFrame(), true);
        Body body = Collision.createBody(Collision.dynamicDef, Collision.wallFixture);
        PolygonShape shape = (PolygonShape) body.getFixtureList().get(0).getShape();
        // TODO: Clean this and use circleShape
        shape.setAsBox((sprite.getWidth() / 2 - 0.3f) * Collision.worldTobox2d, (sprite.getHeight() / 2f - 0.3f) * Collision.worldTobox2d);
        body.setTransform(new Vector2(pos.x, pos.y).scl(Collision.worldTobox2d), 0);
        // prevents the player from rotating about when it collides with other objects.
        body.setFixedRotation(true);
        mapBox2d.get(e).body = body;
        mapGrid.get(e).snapToGrid = false;
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
        mapGrid = world.getMapper(GridPositionComponent.class);
        mapBox2d = world.getMapper(Box2dComponent.class);
        mapSprite = world.getMapper(SpriteComponent.class);
        mapAnimation = world.getMapper(AnimationComponent.class);
        mapLabel = world.getMapper(LabelComponent.class);
        mapKillable = world.getMapper(KillableComponent.class);
        mapPlayer = world.getMapper(PlayerComponent.class);
        mapTimer = world.getMapper(TimerComponent.class);
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
                .build(world);
    }

    @Override
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    @Override
    public int createFromMessage(String message) {
        // TODO: assign name player to label here
        return 0;
    }
}
