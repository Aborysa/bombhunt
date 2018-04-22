package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.bombhunt.game.model.ecs.components.LabelComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;


public class LabelSystem extends IteratingSystem {
    private ComponentMapper<LabelComponent> mapLabel;
    private ComponentMapper<SpriteComponent> mapSprite;

    public LabelSystem() {
        super(Aspect.all(LabelComponent.class, SpriteComponent.class));
    }

    @Override
    protected void process(int e) {
        // IMPORTANT: the update of the position is now performed into the interface directly using
        // controller.getPlayerPosition()
        // will avoid intermittent flickering of the label by using the same player position for
        // camera and label
        // will still replace the position attribute for height_offset that could be useful for
        // animation with varying height...?!
        // otherwise just make check if null and update - more efficient...?
        LabelComponent labelComponent = mapLabel.get(e);
        if (labelComponent.offset_y == 0f) {
            SpriteComponent spriteComponent = mapSprite.get(e);
            labelComponent.offset_y = -spriteComponent.sprite.getHeight() / 1.5f ;
        }
    }
}
