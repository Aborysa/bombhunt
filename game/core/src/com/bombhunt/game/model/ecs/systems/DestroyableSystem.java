package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.DestroyableComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

public class DestroyableSystem extends IteratingSystem {
    private ComponentMapper<DestroyableComponent> mapDestroyable;
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<Box2dComponent> mapBox2d;

    private com.badlogic.gdx.physics.box2d.World box2d;

    public DestroyableSystem(com.badlogic.gdx.physics.box2d.World box2d) {
        super(Aspect.all(TransformComponent.class, DestroyableComponent.class, Box2dComponent.class));
        this.box2d = box2d;
    }

    @Override
    protected void process(int e) {
        DestroyableComponent destroyableComponent = mapDestroyable.get(e);
        if (destroyableComponent.health <= 0) {
            box2d.destroyBody(mapBox2d.get(e).body);
            world.delete(e);
        }
    }
}
