package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

public class TimerSystem extends IteratingSystem {
    private ComponentMapper<TimerComponent> mapTimer;
    private ComponentMapper<TransformComponent> mapTransform;

    public TimerSystem() {
        super(Aspect.all(TimerComponent.class));
    }

    @Override
    protected void process(int e) {
        TimerComponent timerComponent = mapTimer.get(e);
        decreaseTimer(timerComponent);
        raiseEndTimer(timerComponent);
    }

    private void decreaseTimer(TimerComponent timerComponent) {
        float delta = world.getDelta();
        timerComponent.timer -= delta;
    }

    private void raiseEndTimer(TimerComponent timerComponent) {
        if(timerComponent.timer <= 0){
            Event event = createEvent();
            timerComponent.listener.handle(event);
        }
    }

    private Event createEvent() {
        return new Event();
    }
}
