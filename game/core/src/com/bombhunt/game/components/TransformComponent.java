package com.bombhunt.game.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;


public class TransformComponent extends Component{

    // physical position of entity
    // depth = position.z
    public Vector3 position;

    // physical rotation of entity
    public float rotation;

    public Vector2 scale;

    // adding this component to entity, can either specify where to add it
    public TransformComponent(Vector3 position, Vector2 scale, float rotation){
        this.position = position.cpy();
        this.scale = scale.cpy();
        this.rotation = rotation;
    }

    public TransformComponent(){
        this(Vector3.Zero, new Vector2(1f, 1f), 0);
    }

}
