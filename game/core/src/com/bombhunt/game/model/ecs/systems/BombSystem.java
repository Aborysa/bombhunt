package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.BombFactory;

public class BombSystem extends IteratingSystem {
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<TimerComponent> mapTimer;
    private ComponentMapper<TransformComponent> mapTransform;

    private BombFactory bombFactory;

    public BombSystem(BombFactory bombFactory) {
        super(Aspect.all(TransformComponent.class, BombComponent.class, TimerComponent.class));
        this.bombFactory = bombFactory;
    }
    
    @Override
    protected void process(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        bombFactory.createExplosion(transformComponent.position, 0.5f);
    }
}
