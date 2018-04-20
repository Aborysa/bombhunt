package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.BombFactory;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.audio.AudioPlayer;

public class PlayerSystem extends IteratingSystem {

    private ComponentMapper<Box2dComponent> mapBox2D;
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<PlayerComponent> mapPlayer;
    private ComponentMapper<KillableComponent> mapKillable;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<GridPositionComponent> mapGrid;
    private ComponentMapper<BombComponent> mapBomb;

    private BombFactory bombFactory;

    // IMPORTANT: For controller interactions
    private Vector3 last_position = new Vector3();
    private Vector2 last_orientation = new Vector2();
    private boolean bombPlanted = false;

    public PlayerSystem(BombFactory bombFactory) {
        super(Aspect.all(
                TransformComponent.class,
                Box2dComponent.class,
                PlayerComponent.class,
                TimerComponent.class));
        this.bombFactory = bombFactory;
    }

    protected void process(int e) {
        Box2dComponent box2dComponent = mapBox2D.get(e);
        TransformComponent transformComponent = mapTransform.get(e);
        PlayerComponent playerComponent = mapPlayer.get(e);
        if(playerComponent != null){
            // TODO: use velocity component for that?
            // TODO: to be wrapped in a method
            Body body = box2dComponent.body;
            Vector2 velocity = last_orientation.cpy().scl(playerComponent.movement_speed);
            body.setLinearVelocity(velocity);
            // body.applyLinearImpulse(velocity, new Vector2(0,0), true);
            last_position = transformComponent.position.cpy();
            updatePlantedBomb(playerComponent);
            updateCoolDownBomb(playerComponent);
        }
    }

    private void updatePlantedBomb(PlayerComponent playerComponent) {
        if (bombPlanted) {
            bombPlanted = false;
            if (!playerComponent.isCooledDownBomb) {
                playerComponent.isCooledDownBomb = true;
                Vector3 position = last_position.cpy();
                bombFactory.createBomb(position);
                playSoundDropBomb();
            }
        }
    }

    private void playSoundDropBomb() {
        Assets asset_manager = Assets.getInstance();
        Sound sound = asset_manager.get("drop.wav", Sound.class);
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        audioPlayer.playSound(sound);
    }

    private void updateCoolDownBomb(PlayerComponent playerComponent) {
        if (playerComponent.isCooledDownBomb) {
            float delta = world.getDelta();
            playerComponent.ttl_cooldown_bomb -= delta;
            if (playerComponent.ttl_cooldown_bomb <= 0) {
                playerComponent.isCooledDownBomb = false;
                playerComponent.ttl_cooldown_bomb = playerComponent.cooldown_bomb;
            }
        }
    }

    public void move(Vector2 new_orientation) {
        last_orientation = new_orientation;
    }

    public Vector3 getPosition() {
        return last_position;
    }

    public void plantBomb() {
        bombPlanted = true;
    }
}
