package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
import com.bombhunt.game.model.ecs.components.NetworkComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

public class KillableSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<KillableComponent> mapKillable;
    private ComponentMapper<Box2dComponent> mapBox2d;
    private ComponentMapper<NetworkComponent> mapNetwork;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<PlayerComponent> mapPlayer;


    public KillableSystem() {
        super(Aspect.all(TransformComponent.class,
                KillableComponent.class,
                SpriteComponent.class,
                PlayerComponent.class));
    }

    @Override
    protected void process(int e) {
        KillableComponent killableComponent = mapKillable.get(e);
        NetworkComponent networkComponent = mapNetwork.getSafe(e, null);
        PlayerComponent playerComponent = mapPlayer.getSafe(e, null);
        SpriteComponent spriteComponent = mapSprite.get(e);
        float delta = world.getDelta();
        int total_damage = playerComponent.malus;
        killableComponent.ttl_timer -= delta;
        if (killableComponent.ttl_timer <= 0) {
            killableComponent.ttl_timer = killableComponent.timer_damage;
            total_damage += killableComponent.damage_received;
        }
        killableComponent.health -= total_damage;
        if (!killableComponent.is_colored) {
            if (playerComponent.malus > 0) {
                // POISON
                killableComponent.is_colored = true;
                spriteComponent.sprite.setColor(0.5f, 0.5f, 1, 1);
            } else if (total_damage > 0) {
                // NORMAL BOMB_DAMAGE
                killableComponent.is_colored = true;
                spriteComponent.sprite.setColor(1, 0.5f, 0.5f, 1);
            } else if (total_damage < 0) {
                // HEAL
                killableComponent.is_colored = true;
                spriteComponent.sprite.setColor(0.5f, 1, 0.5f, 1);
            }
            if (killableComponent.is_colored) {
                killableComponent.ttl_color = killableComponent.color_persistence;
            }
        } else {
            killableComponent.ttl_color -= delta;
            if (killableComponent.ttl_color <= 0) {
                killableComponent.is_colored = false;
                spriteComponent.sprite.setColor(1, 1, 1, 1);
            }
        }
        if (killableComponent.health <= 0 && (networkComponent != null && networkComponent.isLocal)) {
            playerComponent.is_dead = true;
            playerComponent.last_hit = killableComponent.last_hit;
        }
        playerComponent.malus = 0;
        killableComponent.damage_received = 0;
    }
}
