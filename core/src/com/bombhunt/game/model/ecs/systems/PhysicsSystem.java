package com.bombhunt.game.model.ecs.systems;


import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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
    private ComponentMapper<VelocityComponent> mapNetwork;

    private World box2d;

    public PhysicsSystem(World box2d) {
        super(Aspect.all(TransformComponent.class).one(VelocityComponent.class, Box2dComponent.class));
        this.box2d = box2d;
    }

    @Override
    protected void removed(int e){
        super.removed(e);
        if(mapBox2d.has(e)){
            box2d.destroyBody(mapBox2d.get(e).body);
        }
    }

    protected void process(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        float delta = world.getDelta();
        // Is this component managed by the box2d physics engine?
        if (mapBox2d.has(e)) {
            // Update transform
            Box2dComponent box2dComponent = mapBox2d.get(e);
            Body body = box2dComponent.body;
            if(box2dComponent.targetPos != null && box2dComponent.targetVeloc != null) {
                Vector2 cpos = body.getPosition().cpy();
                Vector2 cveloc = body.getLinearVelocity().cpy();
                
                cpos.lerp(box2dComponent.targetPos, box2dComponent.interpolationValue);
                cveloc.lerp(box2dComponent.targetVeloc, box2dComponent.interpolationValue);
                body.setTransform(cpos, body.getAngle());
                body.setLinearVelocity(cveloc);
            }
            transformComponent.rotation = MathUtils.radiansToDegrees * body.getAngle();


            transformComponent.position.set(body.getPosition().scl(Collision.box2dToWorld), transformComponent.position.z);

        } else if (mapVelocity.has(e)) {
            VelocityComponent velocityComponent = mapVelocity.get(e);
            transformComponent.position.add(new Vector3(velocityComponent.velocity, 0).scl(delta));
        }

    }
}
