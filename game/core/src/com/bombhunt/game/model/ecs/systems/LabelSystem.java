package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.bombhunt.game.model.ecs.components.LabelComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

public class LabelSystem extends IteratingSystem {
    private ComponentMapper<LabelComponent> mapLabel;
    private ComponentMapper<TransformComponent> mapTransform;

    public LabelSystem() {
        super(Aspect.all(TransformComponent.class, LabelComponent.class));
    }

    @Override
    protected void process(int e) {
        LabelComponent labelComponent = mapLabel.get(e);
        TransformComponent transformComponent = mapTransform.get(e);
        labelComponent.position = transformComponent.position.cpy();
        // TODO: OFFSET
    }
}
