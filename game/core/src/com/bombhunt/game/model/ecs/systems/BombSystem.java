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
import com.bombhunt.game.model.ecs.components.TimerComponent;
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
    private ComponentMapper<TimerComponent> mapTimer;
    private ComponentMapper<GridPositionComponent> mapGrid;

    private TextureRegion region;

    public BombSystem() {
        super(Aspect.all(TransformComponent.class,
                BombComponent.class,
                TimerComponent.class,
                GridPositionComponent.class));
        Assets asset_manager = Assets.getInstance();
        region = asset_manager.get("textures/tilemap1.atlas",
                TextureAtlas.class).findRegion("bomb_party_v4");
    }

    @Override
    protected void process(int e) {
        float delta = world.getDelta();
        BombComponent bombComponent = mapBomb.get(e);
        bombComponent.timer -= delta;
        if (bombComponent.timer <= 0) {
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
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(ExplosionComponent.class)
                .add(TimerComponent.class)
                .add(GridPositionComponent.class)
                .build(world);
        int e = world.create(explosionArchetype);
        ExplosionComponent explosionComponent = mapExplosion.get(e);
        //explosionComponent.is_decaded = true;
        float duration = explosionComponent.duration;
        mapTransform.get(e).position = position;
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
