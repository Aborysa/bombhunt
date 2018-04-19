package com.bombhunt.game.model.ecs.systems;


import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.bombhunt.game.services.physic.Collision;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.components.VelocityComponent;


public class PhysicsSystem extends IteratingSystem {

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<Box2dComponent> mapBox2d;
    private ComponentMapper<VelocityComponent> mapVelocity;

    private World box2d;

    public PhysicsSystem(World box2d) {
        super(Aspect.all(TransformComponent.class).one(VelocityComponent.class, Box2dComponent.class));
        this.box2d = box2d;
    }


    protected void process(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        float delta = world.getDelta();
        // Is this component managed by the box2d physics engine?
        if (mapBox2d.has(e)) {
            // Update transform
            Box2dComponent box2dComponent = mapBox2d.get(e);
            Body body = box2dComponent.body;

            transformComponent.rotation = MathUtils.radiansToDegrees * body.getAngle();


            // May need to map box2d coords to ecs coords
            transformComponent.position.set(body.getPosition().scl(Collision.box2dToWorld), transformComponent.position.z);

        } else if (mapVelocity.has(e)) {
            VelocityComponent velocityComponent = mapVelocity.get(e);
            transformComponent.position.add(new Vector3(velocityComponent.velocity, 0).scl(delta));
        }

    }
}
