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
        // substract time remaining of how long the explosion effect should be in the game, and delete the explosion entity if it's ran out
        TimerComponent timerComponent = mapTimer.get(e);
        float delta = world.getDelta();

        timerComponent.timer -= delta;

        // explosion timer is over and we can delete the entity
        if(timerComponent.timer <= 0){
            world.delete(e);
        }

        // TODO: check if player is in bounds and hit the player if it is

    }
}
