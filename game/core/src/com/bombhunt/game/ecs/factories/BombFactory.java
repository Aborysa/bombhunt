package com.bombhunt.game.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.ecs.components.AnimationComponent;
import com.bombhunt.game.ecs.components.BombComponent;
import com.bombhunt.game.ecs.components.ExplosionComponent;
import com.bombhunt.game.ecs.components.SpriteComponent;
import com.bombhunt.game.ecs.components.TimerComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.utils.Assets;
import com.bombhunt.game.utils.SpriteHelper;

import java.util.Timer;

/**
 * Created by erlin on 27.03.2018.
 */

public class BombFactory implements IEntityFactory {

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<TimerComponent> mapTimer;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<ExplosionComponent> mapExplosion;

    private Archetype bombArchetype;
    private Archetype explosionArchetype;

    private World world;

    public int createBomb(Vector3 pos, float timer) {
        int e = world.create(bombArchetype);
        mapTransform.get(e).position = pos;

        // TODO: add sprite/animation
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(Assets.getInstance().get("textures/tilemap1.atlas", TextureAtlas.class).findRegion("bomb_party_v4"),
                        16, 4, 18, 6),
                2);


        //mapSprite.get(e).sprite = Decal.newDecal(new TextureRegion(new Texture("textures/badlogic.jpg")));
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapTransform.get(e).scale = new Vector2(1f, 1f);

        mapTimer.get(e).timer = timer;
        return e;
    }

    public int createExplosion(Vector3 pos, float timer) {
        int e = world.create(explosionArchetype);
        mapTransform.get(e).position = pos;

        // TODO: add sprite/animation
        //mapSprite.get(e).sprite
        //mapAnimation.get(e).animation

        mapTimer.get(e).timer = timer;
        return e;
    }



    // dunno if we can use this in some way fancy to create new bombs as the map loads or something
    @Override
    public int createFromTile(TiledMapTileLayer.Cell cell, TiledMapTileLayer layer, int x, int y, int depth) {
        return 0;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;

        mapTransform = world.getMapper(TransformComponent.class);
        mapAnimation = world.getMapper(AnimationComponent.class);
        mapSprite = world.getMapper(SpriteComponent.class);
        mapBomb = world.getMapper(BombComponent.class);
        mapTimer = world.getMapper(TimerComponent.class);
        mapExplosion = world.getMapper(ExplosionComponent.class);

        bombArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(BombComponent.class)
                .add(TimerComponent.class)
                .build(world);

        explosionArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(ExplosionComponent.class)
                .add(TimerComponent.class)
                .build(world);
    }
}
