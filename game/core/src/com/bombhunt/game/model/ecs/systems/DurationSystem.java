package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

public class DurationSystem extends IteratingSystem {
    private ComponentMapper<TimerComponent> mapTimer;
    private ComponentMapper<TransformComponent> mapTransform;

    public DurationSystem() {
        super(Aspect.all(TimerComponent.class));
    }

    @Override
    protected void process(int e) {
        TimerComponent timerComponent = mapTimer.get(e);
        decreaseTimer(timerComponent);
        if (isTimerOver(timerComponent)) {
            raiseEndTimer(timerComponent);
        }
    }

    private void decreaseTimer(TimerComponent timerComponent) {
        float delta = world.getDelta();
        timerComponent.timer -= delta;
    }

    private Boolean isTimerOver(TimerComponent timerComponent) {
        if (timerComponent.timer <= 0) {
            return true;
        } else {
            return false;
        }
    }

    private void raiseEndTimer(TimerComponent timerComponent) {
        Event event = createEvent();
        if (timerComponent.listener != null) {
            timerComponent.listener.handle(event);
        }
    }

    private Event createEvent() {
        return new Event();
    }
}
