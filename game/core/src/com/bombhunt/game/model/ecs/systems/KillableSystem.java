package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.DestroyableComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
import com.bombhunt.game.model.ecs.components.NetworkComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

public class KillableSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<KillableComponent> mapKillable;
    private ComponentMapper<Box2dComponent> mapBox2d;
    private ComponentMapper<NetworkComponent> mapNetwork;

    private com.badlogic.gdx.physics.box2d.World box2d;

    public KillableSystem(com.badlogic.gdx.physics.box2d.World box2d) {
        super(Aspect.all(TransformComponent.class,
                KillableComponent.class,
                Box2dComponent.class));
        this.box2d = box2d;
    }

    @Override
    protected void process(int e) {
        KillableComponent killableComponent = mapKillable.get(e);
        NetworkComponent networkComponent = mapNetwork.getSafe(e, null);
        float delta = world.getDelta();

        killableComponent.ttl_timer -= delta;
        if (killableComponent.ttl_timer <= 0) {
            killableComponent.ttl_timer = killableComponent.timer_damage;
            killableComponent.health -= killableComponent.damage_received;
        }
        if (killableComponent.health <= 0 && (networkComponent != null && networkComponent.isLocal)) {
            world.delete(e);
        }
        killableComponent.damage_received = 0;
    }
}
