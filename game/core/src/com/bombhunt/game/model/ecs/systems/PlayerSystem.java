package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.BombFactory;

public class PlayerSystem extends IteratingSystem{

    private World box2d;
    private ComponentMapper<Box2dComponent> mapBox2D;
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<PlayerComponent> mapPlayer;
    //private ComponentMapper<VelocityComponent> mapVelocity;

    private Button bombButton;
    private BombFactory bombFactory;
    private Vector2 last_orientation = new Vector2();

    public PlayerSystem(World box2d, Button bombButton, BombFactory bombFactory) {
        // TODO: should create instance here instead of having to pass them...
        super(Aspect.all(TransformComponent.class, Box2dComponent.class, PlayerComponent.class));
        this.box2d = box2d;
        this.bombButton = bombButton;
        this.bombFactory = bombFactory;
    }

    protected void process(int e){
        Box2dComponent box2dComponent = mapBox2D.get(e);
        TransformComponent transformComponent = mapTransform.get(e);
        PlayerComponent playerComponent = mapPlayer.get(e);
        //VelocityComponent velocityComponent = mapVelocity.get(e);

        Body body = box2dComponent.body;

        Vector2 velocity = last_orientation.cpy().scl(playerComponent.movement_speed);
        body.setLinearVelocity(velocity);

        if(bombButton.isPressed()){
            float position_x = transformComponent.position.x;
            float position_y = transformComponent.position.y;
            Vector3 position = new Vector3(position_x, position_y, 0);
            bombFactory.createBomb(position, 3);
        }
    }

    public void move(Vector2 new_orientation) {
        last_orientation = new_orientation;
    }

}
