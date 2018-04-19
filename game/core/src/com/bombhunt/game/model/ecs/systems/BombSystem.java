package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.ExplosionFactory;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.audio.AudioPlayer;

public class BombSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<ExplosionComponent> mapExplosion;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<GridPositionComponent> mapGrid;

    private ExplosionFactory explosionFactory;
    private TextureRegion region;

    public BombSystem(ExplosionFactory explosionFactory) {
        super(Aspect.all(TransformComponent.class,
                BombComponent.class,
                GridPositionComponent.class));
        Assets asset_manager = Assets.getInstance();
        region = asset_manager.get("textures/tilemap1.atlas",
                TextureAtlas.class).findRegion("bomb_party_v4");
        this.explosionFactory = explosionFactory;
    }

    @Override
    protected void process(int e) {
        float delta = world.getDelta();
        BombComponent bombComponent = mapBomb.get(e);
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        System.out.println("BOMB POSITION");
        System.out.println(gridPositionComponent.cellIndex);
        System.out.println(gridPositionComponent.grid);
        bombComponent.ttl_timer -= delta;
        if (bombComponent.ttl_timer <= 0) {
            bombComponent.ttl_timer = bombComponent.timer;
            explodeBomb(e);
        }
    }

    private void explodeBomb(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        explosionFactory.createExplosion(transformComponent.position);
        world.delete(e);
        playSoundExplosion();
    }

    private void playSoundExplosion() {
        Assets asset_manager = Assets.getInstance();
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        Sound sound = asset_manager.get("explosion.wav", Sound.class);
        audioPlayer.playSound(sound);
    }
}
