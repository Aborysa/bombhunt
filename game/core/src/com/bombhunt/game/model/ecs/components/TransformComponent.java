package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;


@DelayedComponentRemoval
public class TransformComponent extends Component {

    // depth = position.z
    public Vector3 position;

    public float rotation;

    public Vector2 scale;

    public TransformComponent(Vector3 position, Vector2 scale, float rotation) {
        this.position = position.cpy();
        this.scale = scale.cpy();
        this.rotation = rotation;
    }

    public TransformComponent() {
        this(Vector3.Zero, new Vector2(1f, 1f), 0);
    }

}
