package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.audio.AudioPlayer;
import com.bombhunt.game.services.graphics.SpriteHelper;

/**
 * Created by erlin on 27.03.2018.
 */

public class BombFactory implements IEntityFactory {

    private final float TIMER_EXPLOSION = 0.5f;

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<TimerComponent> mapTimer;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<ExplosionComponent> mapExplosion;

    private Archetype bombArchetype;
    private Archetype explosionArchetype;

    private World world;

    public int createBomb(Vector3 position, float timer) {
        final int e = world.create(bombArchetype);
        mapTransform.get(e).position = position;
        Assets asset_manager = Assets.getInstance();
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(asset_manager.get("textures/tilemap1.atlas",
                        TextureAtlas.class).findRegion("bomb_party_v4"),
                        16, 4, 18, 6),
                6 / timer);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapTransform.get(e).scale = new Vector2(1f, 1f);
        setUpTimerBomb(e, timer);
        Sound sound = asset_manager.get("drop.wav", Sound.class);
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        audioPlayer.playSound(sound);
        return e;
    }

    private void setUpTimerBomb(int e, float timer) {
        TimerComponent timerComponent = mapTimer.get(e);
        timerComponent.timer = timer;
        timerComponent.listener = new EventListener() {
            @Override
            public boolean handle(Event event) {
                TransformComponent transformComponent = mapTransform.get(e);
                createExplosion(transformComponent.position, TIMER_EXPLOSION);
                world.delete(e);
                return true;
            }
        };
    }

    public int createExplosion(Vector3 pos, float timer) {
        final int e = world.create(explosionArchetype);
        mapTransform.get(e).position = pos;
        mapTransform.get(e).rotation = 90f * (float) Math.random();
        Assets asset_manager = Assets.getInstance();
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(asset_manager.get("textures/tilemap1.atlas",
                        TextureAtlas.class).findRegion("bomb_party_v4"),
                        16, 4, 13, 3),
                3 / timer);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapTransform.get(e).scale = new Vector2(5f, 5f);
        setUpTimerExplosion(e, timer);
        // TODO: clean sounds effects
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        Sound sound = asset_manager.get("explosion.wav", Sound.class);
        audioPlayer.playSound(sound);
        return e;
    }

    private void setUpTimerExplosion(int e, float timer) {
        TimerComponent timerComponent = mapTimer.get(e);
        timerComponent.timer = timer;
        timerComponent.listener = new EventListener() {
            @Override
            public boolean handle(Event event) {
                world.delete(e);
                return true;
            }
        };
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
