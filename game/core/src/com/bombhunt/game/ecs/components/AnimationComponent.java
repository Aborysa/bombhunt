package com.bombhunt.game.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

public class AnimationComponent extends Component {
    public Animation<Decal> animation;
    public float time = 0;
}
