package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

@DelayedComponentRemoval
public class Box2dComponent extends Component {
    public Body body;
    // Only used with NetworkingComponent in order to interpolate
    public Vector2 targetVeloc;
    public Vector2 targetPos;
    public float interpolationValue = 0.25f;
}
