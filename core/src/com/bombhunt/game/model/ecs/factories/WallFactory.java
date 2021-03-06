package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.SolidComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.physic.Collision;

public class WallFactory implements IEntityFactory, ITileFactory {
    private World world;
    public Archetype wallArchtype;
    private Grid grid;

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<GridPositionComponent> mapGrid;
    private ComponentMapper<SolidComponent> mapSolid;
    private ComponentMapper<Box2dComponent> mapBox2d;


    public int createFromTile(Cell cell, TiledMapTileLayer layer, int x, int y, int depth) {
        TiledMapTile tile = cell.getTile();
        TextureRegion tex = tile.getTextureRegion();
        float rotation = 90 * cell.getRotation();

        Decal decal = Decal.newDecal(tex, true);

        Vector3 pos = new Vector3(layer.getTileWidth() * x, layer.getTileHeight() * y, depth).add(new Vector3(layer.getTileWidth() / 2f, layer.getTileHeight() / 2f, 0));
        int e = createWall(pos, decal);

        mapTransform.get(e).rotation = rotation;
        mapGrid.get(e).grid = grid;

        MapProperties props = layer.getProperties();
        Vector2 veloc = new Vector2(props.get("velocity", 0.0f, Float.class), 0f);

        //NOTE: box2d has a hardcoded max speed of 120 units per second
        mapBox2d.get(e).body.setLinearVelocity(veloc);

        return e;
    }

    public void setWorld(World world) {
        this.world = world;
        mapTransform = world.getMapper(TransformComponent.class);
        mapSprite = world.getMapper(SpriteComponent.class);
        mapGrid = world.getMapper(GridPositionComponent.class);
        mapSolid = world.getMapper(SolidComponent.class);
        mapBox2d = world.getMapper(Box2dComponent.class);
        wallArchtype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                .add(GridPositionComponent.class)
                .add(SolidComponent.class)
                .add(Box2dComponent.class)
                .build(world);
    }

    public int createWall(Vector3 position, Decal sprite) {
        int e = world.create(wallArchtype);
        mapSprite.get(e).sprite = sprite;
        mapTransform.get(e).position.set(position);
        Body body = Collision.createBody(Collision.saticDef, Collision.wallFixture);
        PolygonShape shape = (PolygonShape) body.getFixtureList().get(0).getShape();
        shape.setAsBox((sprite.getWidth() / 2f - 0.2f) * Collision.worldTobox2d, (sprite.getHeight() / 2f - 0.2f) * Collision.worldTobox2d);
        body.setTransform(new Vector2(position.x, position.y).scl(Collision.worldTobox2d), 0);
        //Vector2 rveloc = new Vector2((float)Math.random() -0.5f , (float)Math.random() -0.5f).nor().scl((float)Math.random()*100f + 40f);
        //body.setLinearVelocity(rveloc);
        mapBox2d.get(e).body = body;
        return e;
    }

    @Override
    public void setGrid(Grid grid) {
        this.grid = grid;
    }
}