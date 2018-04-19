package com.bombhunt.game.model.ecs.systems;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.DestroyableComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.ItemFactory;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.graphics.SpriteHelper;

public class DestroyableSystem extends IteratingSystem {
    private ComponentMapper<DestroyableComponent> mapDestroyable;
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<TimerComponent> mapTimer;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<Box2dComponent> mapBox2d;

    private com.badlogic.gdx.physics.box2d.World box2d;

    private Archetype crateExplosionArchetype;
    private TextureRegion regionCrate;
    private TextureRegion regionExplosion;
    private ItemFactory itemFactory;

    public DestroyableSystem(com.badlogic.gdx.physics.box2d.World box2d, ItemFactory itemFactory) {
        super(Aspect.all(TransformComponent.class, DestroyableComponent.class, Box2dComponent.class));
        this.box2d = box2d;
        this.itemFactory = itemFactory;
        Assets asset_manager = Assets.getInstance();
        regionCrate = new TextureRegion(asset_manager.get("crateExplosion.png", Texture.class));
        regionExplosion = asset_manager.get("textures/tilemap1.atlas", TextureAtlas.class).findRegion("bomb_party_v4");
    }

    @Override
    protected void process(int e) {
        DestroyableComponent destroyableComponent = mapDestroyable.get(e);
        TransformComponent transformComponent = mapTransform.get(e);
        if (destroyableComponent.health <= 0) {
            createCrateExplosion(e);
            if (Math.random()< 0.5f) {
                itemFactory.createRandomItem(transformComponent.position);
            }
            box2d.destroyBody(mapBox2d.get(e).body);
            world.delete(e);
        }
    }

    private void createCrateExplosion(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        DestroyableComponent destroyableComponent = mapDestroyable.get(e);
        crateExplosionArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(TimerComponent.class)
                .build(world);
        fireAnimation(transformComponent, destroyableComponent);
        crateAnimation(transformComponent, destroyableComponent);
    }

    private void fireAnimation(TransformComponent transformComponent, DestroyableComponent destroyableComponent) {
        int explosionEntity = world.create(crateExplosionArchetype);
        mapTransform.get(explosionEntity).position = transformComponent.position;
        mapAnimation.get(explosionEntity).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(regionExplosion, 16, 4, 13, 3),
                3 / destroyableComponent.timer_destruction);
        mapSprite.get(explosionEntity).sprite = mapAnimation.get(explosionEntity).animation.getKeyFrame(0, true);
        TimerComponent timerComponent = mapTimer.get(explosionEntity);
        timerComponent.timer = destroyableComponent.timer_destruction;
        timerComponent.listener = new EventListener() {
            @Override
            public boolean handle(Event event) {
                world.delete(explosionEntity);
                return true;
            }
        };
    }

    private void crateAnimation(TransformComponent transformComponent, DestroyableComponent destroyableComponent) {
        int crateExplosionEntity = world.create(crateExplosionArchetype);
        mapTransform.get(crateExplosionEntity).position = transformComponent.position;
        mapAnimation.get(crateExplosionEntity).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(regionCrate, 16, 0, 0, 3),
                3 / destroyableComponent.timer_destruction);
        mapSprite.get(crateExplosionEntity).sprite = mapAnimation.get(crateExplosionEntity).animation.getKeyFrame(0, true);
        TimerComponent timerComponent = mapTimer.get(crateExplosionEntity);
        timerComponent.timer = destroyableComponent.timer_destruction;
        timerComponent.listener = new EventListener() {
            @Override
            public boolean handle(Event event) {
                world.delete(crateExplosionEntity);
                return true;
            }
        };
    }
}
