package com.bombhunt.game.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.bombhunt.game.ecs.components.Box2dComponent;
import com.bombhunt.game.ecs.components.PlayerInputComponent;
import com.bombhunt.game.ecs.components.TransformComponent;
import com.bombhunt.game.ecs.components.VelocityComponent;

public class PlayerInputSystem extends IteratingSystem{

    //private ComponentMapper<VelocityComponent> mapVelocity;
    private ComponentMapper<Box2dComponent> mapBox2D;
    private ComponentMapper<PlayerInputComponent> mapPlayer;

    private World box2d;
    private Touchpad joystick;

    public PlayerInputSystem(World box2d, Touchpad joystick) {
        super(Aspect.all(TransformComponent.class, Box2dComponent.class, PlayerInputComponent.class));
        this.box2d = box2d;
        this.joystick = joystick;
    }



    protected void process(int e){

        //VelocityComponent velocityComponent = mapVelocity.get(e);
        //PlayerInputComponent playerInputComponent = mapPlayer.get(e);
        Box2dComponent box2dComponent = mapBox2D.get(e);
        Body body = box2dComponent.body;

        // set velocity values based on input touchpad inputs
        float speed = 10;

        // using the joystick for player input

        //velocityComponent.velocity = new Vector2(touchpad.getKnobPercentX()*speed, touchpad.getKnobPercentY()*speed)
        body.setLinearVelocity(new Vector2(joystick.getKnobPercentX()*speed, joystick.getKnobPercentY()*speed));

        //System.out.println(velocityComponent.velocity);



    }
}
