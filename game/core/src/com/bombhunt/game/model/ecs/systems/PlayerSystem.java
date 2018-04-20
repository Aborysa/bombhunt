package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
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
import com.bombhunt.game.model.ecs.factories.BombFactory;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.audio.AudioPlayer;
import com.bombhunt.game.services.graphics.SpriteHelper;

import static java.lang.Math.PI;
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

    private BombFactory bombFactory;

    // IMPORTANT: For controller interactions
    private Vector3 last_position = new Vector3();
    private Vector2 last_orientation = new Vector2();
    private boolean bombPlanted = false;
    private TextureRegion region;
    private Array<Sprite> sprites;

    public PlayerSystem(BombFactory bombFactory) {
        super(Aspect.all(
                TransformComponent.class,
                Box2dComponent.class,
                PlayerComponent.class,
                TimerComponent.class));
        this.bombFactory = bombFactory;
        Assets asset_manager = Assets.getInstance();
        region = asset_manager.get("textures/tilemap1.atlas",
                TextureAtlas.class).findRegion("bomb_party_v4");
        sprites = SpriteHelper.createSprites(region, 16, 0, 17, 10);
    }

    protected void process(int e) {
        Box2dComponent box2dComponent = mapBox2D.get(e);
        TransformComponent transformComponent = mapTransform.get(e);
        PlayerComponent playerComponent = mapPlayer.get(e);
        KillableComponent killableComponent = mapKillable.get(e);
        SpriteComponent spriteComponent = mapSprite.get(e);
        killableComponent.health = playerComponent.health;
        Body body = box2dComponent.body;
        Vector2 velocity = last_orientation.cpy().scl(playerComponent.movement_speed);
        body.setLinearVelocity(velocity);
        last_position = transformComponent.position.cpy();
        updatePlantedBomb(playerComponent);
        updateCoolDownBomb(playerComponent);
        updateDirection(playerComponent);
        // TODO: add animation eventually
        updateSpriteDirection(playerComponent, spriteComponent);
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
            playerComponent.ttl_cooldown_bomb -= delta;
            if (playerComponent.ttl_cooldown_bomb <= 0) {
                playerComponent.isCooledDownBomb = false;
                playerComponent.ttl_cooldown_bomb = playerComponent.cooldown_bomb;
            }
        }
    }

    private void updateDirection(PlayerComponent playerComponent) {
        if (last_orientation.x != 0 && last_orientation.y != 0) {
            double hyp = sqrt(pow(last_orientation.x,2) + pow(last_orientation.y, 2));
            double theta = toDegrees(asin(last_orientation.y/hyp));
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

    private void updateSpriteDirection(PlayerComponent playerComponent, SpriteComponent spriteComponent) {
        Sprite sprite = sprites.get(playerComponent.direction.getFrame());
        sprite.flip(playerComponent.direction.isFlip(), false);
        spriteComponent.sprite = Decal.newDecal(sprite, true);
    }

    public void move(Vector2 new_orientation) {
        last_orientation = new_orientation;
    }

    public Vector3 getPosition() {
        return last_position.cpy();
    }

    public void plantBomb() {
        bombPlanted = true;
    }
}
