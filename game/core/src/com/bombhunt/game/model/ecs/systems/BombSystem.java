package com.bombhunt.game.model.ecs.systems;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.audio.AudioPlayer;
import com.bombhunt.game.services.graphics.SpriteHelper;

public class BombSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<ExplosionComponent> mapExplosion;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<GridPositionComponent> mapGrid;

    private TextureRegion region;

    public BombSystem() {
        super(Aspect.all(TransformComponent.class,
                BombComponent.class,
                GridPositionComponent.class));
        Assets asset_manager = Assets.getInstance();
        region = asset_manager.get("textures/tilemap1.atlas",
                TextureAtlas.class).findRegion("bomb_party_v4");
    }

    @Override
    protected void process(int e) {
        float delta = world.getDelta();
        BombComponent bombComponent = mapBomb.get(e);
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        System.out.println("BOMB POSITION");
        System.out.println(gridPositionComponent.cellIndex);
        bombComponent.ttl_timer -= delta;
        if (bombComponent.ttl_timer <= 0) {
            bombComponent.ttl_timer = bombComponent.timer;
            explodeBomb(e);
        }
    }

    private void explodeBomb(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        createExplosion(transformComponent.position);
        world.delete(e);
        playSoundExplosion();
    }

    private int createExplosion(Vector3 position) {
        Archetype explosionArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(GridPositionComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(ExplosionComponent.class)
                .build(world);
        int e = world.create(explosionArchetype);
        ExplosionComponent explosionComponent = mapExplosion.get(e);
        float duration = explosionComponent.duration;
        mapTransform.get(e).position = position;
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        gridPositionComponent.cellIndex = gridPositionComponent.grid.getCellIndex(position);
        mapTransform.get(e).rotation = 90f * (float) Math.random();
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(region, 16, 4, 13, 3),
                3 / duration);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapTransform.get(e).scale = new Vector2(1f, 1f);
        return e;
    }

    private void playSoundExplosion() {
        Assets asset_manager = Assets.getInstance();
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        Sound sound = asset_manager.get("explosion.wav", Sound.class);
        audioPlayer.playSound(sound);
    }
}
