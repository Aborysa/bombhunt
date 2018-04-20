package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.model.ecs.components.LabelComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;

import sun.security.provider.ConfigFile;

public class LabelSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<LabelComponent> mapLabel;
    private ComponentMapper<SpriteComponent> mapSprite;

    public LabelSystem() {
        super(Aspect.all(TransformComponent.class, LabelComponent.class, SpriteComponent.class));
    }

    @Override
    protected void process(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        LabelComponent labelComponent = mapLabel.get(e);
        SpriteComponent spriteComponent = mapSprite.get(e);
        labelComponent.position = transformComponent.position.cpy();
        labelComponent.position.y -= spriteComponent.sprite.getHeight()/2f;
    }
}
