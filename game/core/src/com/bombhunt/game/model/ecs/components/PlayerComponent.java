package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class PlayerComponent extends Component {
    public float movement_speed = 10f;
    public Vector2 velocity = new Vector2();

    public void move(Vector2 orientation) {
        velocity = orientation.scl(movement_speed);
    }
}
