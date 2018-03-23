package com.bombhunt.game.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.ecs.components.PlayerComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.ecs.components.VelocityComponent;

public class PlayerInputSystem extends IteratingSystem{

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<VelocityComponent> mapVelocity;
    private ComponentMapper<PlayerComponent> mapPlayer;

    public PlayerInputSystem() {
        super(Aspect.all(TransformComponent.class, VelocityComponent.class, PlayerComponent.class));
    }



    protected void process(int e){


        TransformComponent transformComponent = mapTransform.get(e);
        VelocityComponent velocityComponent = mapVelocity.get(e);
        PlayerComponent playerComponent = mapPlayer.get(e);

        // set velocity values based on input.

    }
}
