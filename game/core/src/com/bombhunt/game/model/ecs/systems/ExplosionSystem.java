package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

public class ExplosionSystem extends IteratingSystem {
    private ComponentMapper<ExplosionComponent> mapExplosion;
    private ComponentMapper<TimerComponent> mapTimer;
    private ComponentMapper<TransformComponent> mapTransform;

    public ExplosionSystem() {
        super(Aspect.all(ExplosionComponent.class, TimerComponent.class));
    }

    @Override
    protected void process(int e) {
        // TODO: to be remove and use TimerComponent instead

        // TODO: check if player is in bounds and hit the player if it is

    }
}
