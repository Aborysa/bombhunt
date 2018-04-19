package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.DestroyableComponent;
import com.bombhunt.game.model.ecs.components.LabelComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;

public class LabelSystem extends IteratingSystem {
    private ComponentMapper<LabelComponent> mapLabel;

    public LabelSystem() {
        super(Aspect.all(TransformComponent.class, DestroyableComponent.class, Box2dComponent.class));
    }

    @Override
    protected void process(int e) {
        LabelComponent labelComponent = mapLabel.get(e);
        Assets assetManager = Assets.getInstance();
        assetManager.get("skin/craftacular-ui.json", Skin.class);
    }
}
