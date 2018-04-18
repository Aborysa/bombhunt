package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.bombhunt.game.model.ecs.components.DestroyableComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

public class DestroyableSystem extends IteratingSystem {
    private ComponentMapper<DestroyableComponent> mapDestroyable;
    private ComponentMapper<TransformComponent> mapTransform;

    public DestroyableSystem() {
        super(Aspect.all(TransformComponent.class, DestroyableComponent.class));
    }

    @Override
    protected void process(int e) {
        DestroyableComponent destroyableComponent = mapDestroyable.get(e);
        if (destroyableComponent.health <= 0) {
            System.out.println("DESTRUCTION");
            System.out.println(e + " has been destroyed");
            world.delete(e);
        }
    }
}
