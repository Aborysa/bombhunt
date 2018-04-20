package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

@DelayedComponentRemoval
public class Box2dComponent extends Component {
    public Body body;
    public Vector2 prePos;
}
