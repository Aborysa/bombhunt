package com.bombhunt.game.systems;


import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.components.TransformComponent;
import com.bombhunt.game.components.VelocityComponent;

public class VelocitySystem extends IteratingSystem {


    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<VelocityComponent> mapVelocity;

    public VelocitySystem(){
        super(Aspect.all(TransformComponent.class, VelocityComponent.class));
    }

    protected void process(int e){
        TransformComponent transformComponent = mapTransform.get(e);
        VelocityComponent velocityComponent = mapVelocity.get(e);

        float delta = world.getDelta();
        transformComponent.position.add(new Vector3(velocityComponent.velocity, 0).scl(delta));

    }
}
