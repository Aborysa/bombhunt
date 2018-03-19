package com.bombhunt.game.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;


public class VelocityComponent extends Component {
    public Vector2 velocity;


    public VelocityComponent(Vector2 vec){
        this.velocity = vec.cpy();
    }

    public VelocityComponent(){
        this(Vector2.Zero);
    }
}
