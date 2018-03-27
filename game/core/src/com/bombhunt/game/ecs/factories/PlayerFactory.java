package com.bombhunt.game.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.bombhunt.game.box2d.Collision;
import com.bombhunt.game.ecs.components.Box2dComponent;
import com.bombhunt.game.ecs.components.DestroyableComponent;
import com.bombhunt.game.ecs.components.PlayerInputComponent;
import com.bombhunt.game.ecs.components.SpriteComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.ecs.components.VelocityComponent;
import com.bombhunt.game.ecs.systems.PlayerInputSystem;

/**
 * Created by erlin on 23.03.2018.
 */

public class PlayerFactory implements IEntityFactory{
    private World world;
    public Archetype playerArchtype;

    ComponentMapper<TransformComponent> mapTransform;
    ComponentMapper<SpriteComponent> mapSprite;
    //ComponentMapper<VelocityComponent> mapVelocity;
    ComponentMapper<Box2dComponent> mapBox2d;
    ComponentMapper<PlayerInputComponent> mapPlayerInput;



    public int createPlayer(int x, int y, Decal sprite){
        int e = world.create(playerArchtype);

        mapSprite.get(e).sprite = sprite;
        mapTransform.get(e).position = new Vector3(x, y, 0);
        Body body = Collision.createBody(Collision.dynamicDef, Collision.wallFixture);
        PolygonShape shape = (PolygonShape) body.getFixtureList().get(0).getShape();
        shape.setAsBox((sprite.getWidth()/2 - 0.2f) * Collision.worldTobox2d, (sprite.getHeight()/2f -0.2f) * Collision.worldTobox2d);
        body.setTransform(new Vector2(x, y).scl(Collision.worldTobox2d), 0);

        // prevents the player from rotating about when it collides with other objects.
        body.setFixedRotation(true);
        mapBox2d.get(e).body = body;


        return e;
    }

    @Override
    public int createFromTile(TiledMapTileLayer.Cell cell, TiledMapTileLayer layer, int x, int y, int depth) {
        return 0;
    }

    public void setWorld(World world){
        this.world = world;

        mapTransform = world.getMapper(TransformComponent.class);
        mapSprite = world.getMapper(SpriteComponent.class);
        //mapVelocity = world.getMapper(VelocityComponent.class);
        mapBox2d = world.getMapper(Box2dComponent.class);
        mapPlayerInput = world.getMapper(PlayerInputComponent.class);


        playerArchtype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                //.add(VelocityComponent.class)
                .add(Box2dComponent.class)
                .add(PlayerInputComponent.class)
                .build(world);


    }
}
