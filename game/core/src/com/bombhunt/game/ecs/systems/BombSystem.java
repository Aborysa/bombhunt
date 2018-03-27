package com.bombhunt.game.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.ecs.components.BombComponent;
import com.bombhunt.game.ecs.components.TimerComponent;
import com.bombhunt.game.ecs.components.TransformComponent;

public class BombSystem extends IteratingSystem {
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<TimerComponent> mapTimer;
    private ComponentMapper<TransformComponent> mapTransform;


    public BombSystem() {
        super(Aspect.all(TransformComponent.class, BombComponent.class, TimerComponent.class));


    }
    @Override
    protected void process(int e) {

        TimerComponent timerComponent = mapTimer.get(e);
        float delta = world.getDelta();

        // substract time passed to the timerComponent
        timerComponent.timer -= delta;

        // check if the bomb fuse timer has run out
        if(timerComponent.timer <= 0){
            // call factory to create an explosion
            TransformComponent transformComponent = mapTransform.get(e);

            // TODO: implement the bomb factory class
            //bombFactory.createExplosion(transformComponent.position);


            // delete the bomb entity from the world since the timer has expired.
            world.delete(e);
        }

    }
}
