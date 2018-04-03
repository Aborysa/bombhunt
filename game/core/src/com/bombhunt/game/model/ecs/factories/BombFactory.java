package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.graphics.SpriteHelper;

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

        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(Assets.getInstance().get("textures/tilemap1.atlas",
                        TextureAtlas.class).findRegion("bomb_party_v4"),
                        16, 4, 18, 6),
                6/timer);


        //mapSprite.get(e).sprite = Decal.newDecal(new TextureRegion(new Texture("textures/badlogic.jpg")));
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapTransform.get(e).scale = new Vector2(1f, 1f);

        mapTimer.get(e).timer = timer;
        return e;
    }

    public int createExplosion(Vector3 pos, float timer) {
        System.out.println("creating exp");
        int e = world.create(explosionArchetype);
        mapTransform.get(e).position = pos;
        mapTransform.get(e).rotation = 90f*(float)Math.random();

        // TODO: add sprite/animatio
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(Assets.getInstance().get("textures/tilemap1.atlas",
                        TextureAtlas.class).findRegion("bomb_party_v4"),
                        16, 4, 13, 3),
                3/timer);


        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapTransform.get(e).scale = new Vector2(5f, 5f);

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
