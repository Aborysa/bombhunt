package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.Box2dComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.components.InputComponent;
import com.bombhunt.game.model.ecs.factories.BombFactory;
import com.bombhunt.game.model.ecs.factories.DeathFactory;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.audio.AudioPlayer;
import com.bombhunt.game.services.graphics.SpriteHelper;
import com.bombhunt.game.services.networking.NetworkManager;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.asin;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

public class PlayerSystem extends IteratingSystem {

    private ComponentMapper<Box2dComponent> mapBox2D;
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<PlayerComponent> mapPlayer;
    private ComponentMapper<KillableComponent> mapKillable;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<GridPositionComponent> mapGrid;
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<InputComponent> mapInput;

    private BombFactory bombFactory;
    private DeathFactory deathFactory;
    private com.badlogic.gdx.physics.box2d.World box2d;

    // IMPORTANT: For controller interactions
    private Vector3 last_position = new Vector3();
    private Vector2 last_orientation = new Vector2();
    private Map<STATS_ENUM, Number> stats = new HashMap<>();
    private boolean bombPlanted = false;
    private Array<Array<Sprite>> sprites = new Array<Array<Sprite>>(4);

    public PlayerSystem(BombFactory bombFactory, DeathFactory deathFactory, World box2d) {
        super(Aspect.all(
                TransformComponent.class,
                Box2dComponent.class,
                PlayerComponent.class,
                TimerComponent.class
            )
        );
        this.bombFactory = bombFactory;
        this.deathFactory = deathFactory;
        this.box2d = box2d;
        Assets asset_manager = Assets.getInstance();
        TextureRegion region = asset_manager.get("textures/tilemap1.atlas",
                TextureAtlas.class).findRegion("bomb_party_v4");
            
        for(int i = 0; i < 4; i++)
            sprites.add(SpriteHelper.createSprites(region, 16, 0, 14 + i, 10));
        for (STATS_ENUM i: STATS_ENUM.values()) {
            stats.put(i, 0);
        }
    }

    protected void process(int e) {
        Box2dComponent box2dComponent = mapBox2D.get(e);
        TransformComponent transformComponent = mapTransform.get(e);
        PlayerComponent playerComponent = mapPlayer.get(e);
        Body body = box2dComponent.body;
        if(mapInput.has(e)){
            Vector2 velocity = last_orientation.cpy().scl(playerComponent.movement_speed);
            body.setLinearVelocity(velocity);
            // body.applyLinearImpulse(velocity, new Vector2(0,0), true);
            last_position = transformComponent.position.cpy();
            updatePlantedBomb(playerComponent);
            updateCoolDownBomb(playerComponent);
            updateStats(e);
            updateStilAlive(e);
        }
        updateDirection(e);
    }

    private void updatePlantedBomb(PlayerComponent playerComponent) {
        if (bombPlanted) {
            bombPlanted = false;
            if (!playerComponent.isCooledDownBomb) {
                playerComponent.isCooledDownBomb = true;
                Vector3 position = last_position.cpy();
                bombFactory.createBomb(position,
                        playerComponent.bomb_damage, playerComponent.bomb_range);
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
            playerComponent.ttl_bomb_cooldown -= delta;
            if (playerComponent.ttl_bomb_cooldown <= 0) {
                playerComponent.isCooledDownBomb = false;
                playerComponent.ttl_bomb_cooldown = playerComponent.bomb_cooldown;
            }
        }
    }

    private void updateDirection(int e) {
        PlayerComponent playerComponent = mapPlayer.get(e);
        if(mapInput.has(e)) {
            DIRECTION_ENUM previous_direction = playerComponent.direction;
            if (last_orientation.x != 0 && last_orientation.y != 0) {
                double hyp = sqrt(pow(last_orientation.x, 2) + pow(last_orientation.y, 2));
                double theta = toDegrees(asin(last_orientation.y / hyp));
                if (last_orientation.x > 0) {
                    if (last_orientation.y > 0) {
                        if (theta > 45) {
                            playerComponent.direction = DIRECTION_ENUM.UP;
                        } else {
                            playerComponent.direction = DIRECTION_ENUM.RIGHT;
                        }
                    } else {
                        if (theta < -45) {
                            playerComponent.direction = DIRECTION_ENUM.DOWN;
                        } else {
                            playerComponent.direction = DIRECTION_ENUM.RIGHT;
                        }
                    }
                } else {
                    if (last_orientation.y > 0) {
                        if (theta > 45) {
                            playerComponent.direction = DIRECTION_ENUM.UP;
                        } else {
                            playerComponent.direction = DIRECTION_ENUM.LEFT;
                        }
                    } else {
                        if (theta < -45) {
                            playerComponent.direction = DIRECTION_ENUM.DOWN;
                        } else {
                            playerComponent.direction = DIRECTION_ENUM.LEFT;
                        }
                    }
                }
            }
        }
        if (playerComponent.prev_direction != playerComponent.direction) {
            updateSpriteDirection(e);
            playerComponent.prev_direction = playerComponent.direction;
        }
    }

    private void updateSpriteDirection(int e) {
        PlayerComponent playerComponent = mapPlayer.get(e);
        int frame = playerComponent.direction.getFrame();
        Sprite new_sprite = new Sprite(sprites.get(playerComponent.index).get(frame));
        boolean is_flipped = playerComponent.direction.isFlip();
        new_sprite.flip(is_flipped, false);
        Array<Sprite> new_array_animation = new Array<>();
        new_array_animation.add(new_sprite);
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(new_array_animation, 60);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
    }

    private void updateStats(int e) {
        PlayerComponent playerComponent = mapPlayer.get(e);
        KillableComponent killableComponent = mapKillable.get(e);
        if (playerComponent.is_dead) {
            stats.put(STATS_ENUM.HEALTH, 0);
        } else {
            stats.put(STATS_ENUM.MAX_HEALTH, playerComponent.max_health);
            stats.put(STATS_ENUM.HEALTH, killableComponent.health);
            stats.put(STATS_ENUM.BOMB_COOLDOWN, playerComponent.bomb_cooldown);
            stats.put(STATS_ENUM.BOMB_RANGE, playerComponent.bomb_range);
            stats.put(STATS_ENUM.BOMB_DAMAGE, playerComponent.bomb_damage);
            stats.put(STATS_ENUM.SPEED, playerComponent.movement_speed);
        }
    }

    private void updateStilAlive(int e) {
        if(mapPlayer.get(e).is_dead) {
            TransformComponent transformComponent = mapTransform.get(e);
            Vector3 position = transformComponent.position.cpy();
            PlayerComponent playerComponent = mapPlayer.get(e);
            int frame = playerComponent.direction.getFrame();
            Sprite last_sprite = new Sprite(sprites.get(playerComponent.index).get(frame));
            boolean is_flipped = playerComponent.direction.isFlip();
            last_sprite.flip(is_flipped, false);
            deathFactory.createDeath(position, last_sprite, playerComponent.last_hit);
            box2d.destroyBody(mapBox2D.get(e).body);
            world.delete(e);
        }
    }

    public void move(Vector2 new_orientation) {
        last_orientation = new_orientation;
    }

    public Vector3 getPosition() {
        return last_position.cpy();
    }

    public Map<STATS_ENUM, Number> getStats() {
        return stats;
    }

    public void plantBomb() {
        bombPlanted = true;
    }
}
