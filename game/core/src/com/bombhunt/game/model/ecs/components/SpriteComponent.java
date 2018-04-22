package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.badlogic.gdx.graphics.g3d.decals.Decal;


@DelayedComponentRemoval
public class SpriteComponent extends Component {
    public Decal sprite;
}
