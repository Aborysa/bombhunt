package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.BombFactory;

public class PlayerSystem extends IteratingSystem {

    private float COOLDOWN_BOMB = 1;
    private float BOMB_TIMER = 3;

    private World box2d;
    private ComponentMapper<Box2dComponent> mapBox2D;
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<PlayerComponent> mapPlayer;
    private ComponentMapper<TimerComponent> mapTimer;
    //private ComponentMapper<VelocityComponent> mapVelocity;

    private BombFactory bombFactory;
    private Vector3 last_position = new Vector3();
    private Vector2 last_orientation = new Vector2();
    private Boolean coolDownBomb = false;
    private Boolean bombPlanted = false;

    public PlayerSystem(World box2d, BombFactory bombFactory) {
        // TODO: should create instance here instead of having to pass them...
        super(Aspect.all(
                TransformComponent.class,
                Box2dComponent.class,
                PlayerComponent.class,
                TimerComponent.class));
        this.box2d = box2d;
        this.bombFactory = bombFactory;
    }

    protected void process(int e) {
        Box2dComponent box2dComponent = mapBox2D.get(e);
        TransformComponent transformComponent = mapTransform.get(e);
        PlayerComponent playerComponent = mapPlayer.get(e);
        TimerComponent timerComponent = mapTimer.get(e);
        //VelocityComponent velocityComponent = mapVelocity.get(e);

        // TODO: use velocity component for that?
        Body body = box2dComponent.body;
        Vector2 velocity = last_orientation.cpy().scl(playerComponent.movement_speed);
        body.setLinearVelocity(velocity);

        last_position = transformComponent.position.cpy();

        if (bombPlanted) {
            float position_x = transformComponent.position.x;
            float position_y = transformComponent.position.y;
            Vector3 position = new Vector3(position_x, position_y, 0);
            bombFactory.createBomb(position, BOMB_TIMER);
            startCoolDownBomb(timerComponent);
            bombPlanted = false;
        }
    }

    public void move(Vector2 new_orientation) {
        last_orientation = new_orientation;
    }

    public void plantBomb() {
        if (!coolDownBomb) {
            bombPlanted = true;
        }
    }

    public void startCoolDownBomb(TimerComponent timerComponent) {
        coolDownBomb = true;
        timerComponent.timer = COOLDOWN_BOMB;
        timerComponent.listener = new EventListener() {
            @Override
            public boolean handle(Event event) {
                stopCoolDownBomb();
                return true;
            }
        };
    }

    public void stopCoolDownBomb() {
        coolDownBomb = false;
    }

    public Vector3 getPosition() {
        return last_position;
    }
}
