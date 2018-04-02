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
        //System.out.println("bomb livin");

        TimerComponent timerComponent = mapTimer.get(e);
        float delta = world.getDelta();

        // substract time passed to the timerComponent
        timerComponent.timer -= delta;

        // check if the bomb fuse timer has run out
        if(timerComponent.timer <= 0){
            // call factory to create an explosion
            System.out.println("boom");
            TransformComponent transformComponent = mapTransform.get(e);

            // TODO: implement the bomb factory class
            bombFactory.createExplosion(transformComponent.position, 0.5f);


            // delete the bomb entity from the world since the timer has expired.
            world.delete(e);
        }

    }
}
