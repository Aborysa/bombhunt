package com.bombhunt.game.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimationComponent extends Component {
    public Animation<Sprite> animation;
    public float time = 0;
}
