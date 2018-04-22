package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.DeathComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.graphics.SpriteHelper;

public class DeathSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<DeathComponent> mapDeath;

    private TextureRegion region;

    public DeathSystem() {
        super(Aspect.all(TransformComponent.class,
                SpriteComponent.class,
                DeathComponent.class));
        Assets asset_manager = Assets.getInstance();
        region = new TextureRegion(asset_manager.get("skullMedal.png", Texture.class));
    }

    @Override
    protected void process(int e) {
        DeathComponent deathComponent = mapDeath.get(e);
        if(deathComponent.ttl_timer > 0) {
            float delta = world.getDelta();
            deathComponent.ttl_timer -= delta;
            updateOpacity(e);
            updateBlinking(e);
        } else if(!deathComponent.is_skull_displayed) {
            displaySkullMedal(e);
        }
    }

    private void updateOpacity(int e) {
        DeathComponent deathComponent = mapDeath.get(e);
        SpriteComponent spriteComponent = mapSprite.get(e);
        float updateIntervalTime = deathComponent.timer/deathComponent.update_opacity_frequency;
        if (((int) deathComponent.ttl_timer/updateIntervalTime) % 2 == 1) {
            deathComponent.alpha -= deathComponent.opacity_step;
            spriteComponent.sprite.setColor(1, 1, 1, deathComponent.alpha);
        }
    }

    private void updateBlinking(int e) {
        DeathComponent deathComponent = mapDeath.get(e);
        SpriteComponent spriteComponent = mapSprite.get(e);
        if (deathComponent.ttl_timer <= deathComponent.blinking_time) {
            int visible = (int) (deathComponent.ttl_timer / 0.1f) % 2;
            spriteComponent.sprite.setColor(1, 1, 1, deathComponent.alpha*visible);
        }
    }

    private void displaySkullMedal(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        DeathComponent deathComponent = mapDeath.get(e);
        deathComponent.is_skull_displayed = true;
        Array<Sprite> sprites = SpriteHelper.createSprites(region, 75, 0, 0, 1);
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(sprites, 60);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapSprite.get(e).sprite.setDimensions(8, 8);
        transformComponent.rotation = 0;
    }
}
