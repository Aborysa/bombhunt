//package com.bombhunt.game.model.ecs.factories;
//
//import android.support.annotation.NonNull;
//
//import com.artemis.Archetype;
//import com.artemis.ArchetypeBuilder;
//import com.artemis.Component;
//import com.artemis.ComponentMapper;
//import com.artemis.World;
//import com.artemis.utils.IntBag;
//import com.badlogic.gdx.audio.Sound;
//import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.math.Vector3;
//import com.badlogic.gdx.scenes.scene2d.Event;
//import com.badlogic.gdx.scenes.scene2d.EventListener;
//import com.bombhunt.game.model.Grid;
//import com.bombhunt.game.model.ecs.components.AnimationComponent;
//import com.bombhunt.game.model.ecs.components.BombComponent;
//import com.bombhunt.game.model.ecs.components.DestroyableComponent;
//import com.bombhunt.game.model.ecs.components.ExplosionComponent;
//import com.bombhunt.game.model.ecs.components.GridPositionComponent;
//import com.bombhunt.game.model.ecs.components.SolidComponent;
//import com.bombhunt.game.model.ecs.components.SpriteComponent;
//import com.bombhunt.game.model.ecs.components.TimerComponent;
//import com.bombhunt.game.model.ecs.components.TransformComponent;
//import com.bombhunt.game.services.assets.Assets;
//import com.bombhunt.game.services.audio.AudioPlayer;
//import com.bombhunt.game.services.graphics.SpriteHelper;
//
///**
// * Created by erlin on 27.03.2018.
// */
//
//public class BombFactory implements IEntityFactory {
//
//    private ComponentMapper<TransformComponent> mapTransform;
//    private ComponentMapper<SpriteComponent> mapSprite;
//    private ComponentMapper<BombComponent> mapBomb;
//    private ComponentMapper<TimerComponent> mapTimer;
//    private ComponentMapper<AnimationComponent> mapAnimation;
//    private ComponentMapper<ExplosionComponent> mapExplosion;
//    private ComponentMapper<GridPositionComponent> mapGrid;
//    private ComponentMapper<SolidComponent> mapSolid;
//    private ComponentMapper<DestroyableComponent> mapDestroyable;
//
//    private Archetype bombArchetype;
//    private Archetype explosionArchetype;
//
//    private World world;
//    private Grid grid;
//
//    private TextureRegion region;
//
//    public BombFactory() {
            // TODO: reimplement for networking
//        Assets asset_manager = Assets.getInstance();
//        region = asset_manager.get("textures/tilemap1.atlas",
//                TextureAtlas.class).findRegion("bomb_party_v4");
//    }
//
//    @Override
//    public void setWorld(World world) {
//        this.world = world;
//
//        mapTransform = world.getMapper(TransformComponent.class);
//        mapAnimation = world.getMapper(AnimationComponent.class);
//        mapSprite = world.getMapper(SpriteComponent.class);
//        mapBomb = world.getMapper(BombComponent.class);
//        mapTimer = world.getMapper(TimerComponent.class);
//        mapExplosion = world.getMapper(ExplosionComponent.class);
//        mapGrid = world.getMapper(GridPositionComponent.class);
//        mapSolid = world.getMapper(SolidComponent.class);
//        mapDestroyable = world.getMapper(DestroyableComponent.class);
//
//        bombArchetype = new ArchetypeBuilder()
//                .add(TransformComponent.class)
//                .add(SpriteComponent.class)
//                .add(AnimationComponent.class)
//                .add(BombComponent.class)
//                .add(TimerComponent.class)
//                .add(GridPositionComponent.class)
//                .build(world);
//
//        explosionArchetype = new ArchetypeBuilder()
//                .add(TransformComponent.class)
//                .add(SpriteComponent.class)
//                .add(AnimationComponent.class)
//                .add(ExplosionComponent.class)
//                .add(TimerComponent.class)
//                .add(GridPositionComponent.class)
//                .build(world);
//    }
//
//    @Override
//    public void setGrid(Grid grid) {
//        this.grid = grid;
//    }
//
//
//    @Override
//    public int createFromTile(TiledMapTileLayer.Cell cell, TiledMapTileLayer layer, int x, int y, int depth) {
//        return 0;
//    }
//
//    public int createBomb(Vector3 position) {
//        final int e = world.create(bombArchetype);
//        BombComponent bombComponent = mapBomb.get(e);
//        mapTransform.get(e).position = position;
//        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
//                SpriteHelper.createSprites(region, 16, 4, 18, 6),
//                6 / bombComponent.timer);
//        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
//        mapTransform.get(e).scale = new Vector2(1f, 1f);
//        mapGrid.get(e).grid = grid;
//        setUpTimerBomb(e, bombComponent.timer);
//        playSoundDropBomb();
//        return e;
//    }
//
//    private void setUpTimerBomb(int e, float timer) {
//        TimerComponent timerComponent = mapTimer.get(e);
//        timerComponent.timer = timer;
//        timerComponent.listener = new EventListener() {
//            @Override
//            public boolean handle(Event event) {
//                explodeBomb(e);
//                return true;
//            }
//        };
//    }
//
//    private void playSoundDropBomb() {
//        Assets asset_manager = Assets.getInstance();
//        Sound sound = asset_manager.get("drop.wav", Sound.class);
//        AudioPlayer audioPlayer = AudioPlayer.getInstance();
//        audioPlayer.playSound(sound);
//    }
//
//    private void decadeBomb(int e, int range) {
//        ExplosionComponent explosionComponent = mapExplosion.get(e);
//        TransformComponent transformComponent = mapTransform.get(e);
//        TimerComponent timerComponent = mapTimer.get(e);
//        timerComponent.timer = explosionComponent.time_decade;
//        timerComponent.listener = new EventListener() {
//            @Override
//            public boolean handle(Event event) {
//                Vector3[] dirs = {new Vector3(0, 1, 0),
//                        new Vector3(1, 0, 0),
//                        new Vector3(0, -1, 0),
//                        new Vector3(-1, 0, 0)};
//                for (Vector3 dir : dirs) {
//                    chainExplosion(transformComponent.position, dir, range);
//                }
//                //world.delete(e);
//                return true;
//            }
//        };
//    }
//
//    private void chainExplosion(Vector3 prev_position, Vector3 direction, int range) {
//        Vector3 offset = direction.cpy().scl(grid.getCellSize());
//        Vector3 position = prev_position.cpy().add(offset);
//        boolean hasSolid = false;
//        if (range > 0) {
//            hasSolid = detect(position, mapSolid);
//        }
//        if(!hasSolid) {
//            boolean finalHasSolid = hasSolid;
//            range -= 1;
//            int finalRange = range;
//            int explosionEntity = createSubExplosion(position);
//            ExplosionComponent explosionComponent = mapExplosion.get(explosionEntity);
//            TimerComponent timerComponent = mapTimer.get(explosionEntity);
//            timerComponent.timer = explosionComponent.time_decade;
//            timerComponent.listener = new EventListener() {
//                @Override
//                public boolean handle(Event event) {
//                    if (finalRange > 0 && !finalHasSolid) {
//                        chainExplosion(position, direction, finalRange);
//                    }
//                    //world.delete(explosionEntity);
//                    return true;
//                }
//            };
//        } else {
//            destructionDamage(position);
//        }
//    }
//
//    @NonNull
//    private Boolean detect(Vector3 position, ComponentMapper<? extends Component> mapComponent) {
//        // TODO: Should be moved into grid class
//        IntBag entities = grid.getEntities(grid.getCellIndex(position));
//        for (int i = 0; i < entities.size(); i++) {
//            int e = entities.get(i);
//            if (mapComponent.has(e)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private int createMainExplosion(Vector3 position) {
//        int e = world.create(explosionArchetype);
//        ExplosionComponent explosionComponent = mapExplosion.get(e);
//        float duration = explosionComponent.duration;
//        mapTransform.get(e).position = position;
//        mapTransform.get(e).rotation = 90f * (float) Math.random();
//        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
//                SpriteHelper.createSprites(region, 16, 4, 13, 3), 3 / duration);
//        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
//        mapTransform.get(e).scale = new Vector2(1f, 1f);
//        mapGrid.get(e).grid = grid;
//        return e;
//    }
//
//    private int createSubExplosion(Vector3 position) {
//        int e = createMainExplosion(position);
//        explosionDamage(position);
//        return e;
//    }
//
//    private void explosionDamage(Vector3 position) {
//        IntBag bombsEntities = filterEntities(position, mapBomb);
//        for (int i = 0; i < bombsEntities.size(); i++) {
//            int e = bombsEntities.get(i);
//            explodeBomb(e);
//        }
//    }
//
//    private void playSoundExplosion() {
//        Assets asset_manager = Assets.getInstance();
//        AudioPlayer audioPlayer = AudioPlayer.getInstance();
//        Sound sound = asset_manager.get("explosion.wav", Sound.class);
//        audioPlayer.playSound(sound);
//    }
//
//    private void destructionDamage(Vector3 position) {
//        IntBag destroyableEntities = filterEntities(position, mapDestroyable);
//        for (int i = 0; i < destroyableEntities.size(); i++) {
//            int e = destroyableEntities.get(i);
//            mapDestroyable.get(e).health -= 1;
//        }
//        //TODO else if (hasHealth) {health -= damage}
//    }
//
//    private IntBag filterEntities(Vector3 position, ComponentMapper<? extends Component> mapComponent) {
//        // TODO: to be moved into grid class
//        int cellIndex = grid.getCellIndex(position);
//        IntBag entities = grid.getEntities(cellIndex);
//        IntBag matchingEntities = new IntBag();
//        for (int e : entities.getData()) {
//            if (mapComponent.has(e)) {
//                matchingEntities.add(e);
//            }
//        }
//        return matchingEntities;
//    }
//
////    private void playSoundDropBomb() {
////        Assets asset_manager = Assets.getInstance();
////        Sound sound = asset_manager.get("drop.wav", Sound.class);
////        AudioPlayer audioPlayer = AudioPlayer.getInstance();
////        audioPlayer.playSound(sound);
////    }
//}
