package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.bombhunt.game.model.ecs.components.DeathComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

public class DeathSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<DeathComponent> mapDeath;

    public DeathSystem() {
        super(Aspect.all(TransformComponent.class,
                SpriteComponent.class,
                DeathComponent.class));
    }

    @Override
    protected void process(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        SpriteComponent spriteComponent = mapSprite.get(e);
        DeathComponent deathComponent = mapDeath.get(e);
        float delta = world.getDelta();
        deathComponent.ttl_timer -= delta;

        // reduce opacity level
        if ((deathComponent.ttl_timer/(deathComponent.timer/20)) % 2 == 1) {
            deathComponent.alpha -= 0.05f;
            spriteComponent.sprite.setColor(1, 1, 1, deathComponent.alpha);
        }
        // flickering high rate at the end
        if (deathComponent.ttl_timer <= deathComponent.blinking_time) {
            int visible = (int) (deathComponent.ttl_timer / 0.1f) % 2;
            spriteComponent.sprite.setColor(1, 1, 1, deathComponent.alpha*visible);
        }

        if(deathComponent.ttl_timer <= 0) {
            // SPAWN SKULL AT POSITION
            // REMOVE ENTITY
            world.delete(e);
        }
    }
}
