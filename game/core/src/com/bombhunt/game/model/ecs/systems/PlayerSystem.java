package com.bombhunt.game.model.ecs.systems;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
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
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.audio.AudioPlayer;
import com.bombhunt.game.services.graphics.SpriteHelper;

public class PlayerSystem extends IteratingSystem {

    private World box2d;
    private ComponentMapper<Box2dComponent> mapBox2D;
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<PlayerComponent> mapPlayer;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<GridPositionComponent> mapGrid;
    private ComponentMapper<BombComponent> mapBomb;

    // TODO: move to component...
    private Vector3 last_position = new Vector3();
    private Vector2 last_orientation = new Vector2();

    private TextureRegion region;
    private boolean bombPlanted = false;

    public PlayerSystem(World box2d) {
        super(Aspect.all(
                TransformComponent.class,
                Box2dComponent.class,
                PlayerComponent.class,
                TimerComponent.class));
        this.box2d = box2d;
        Assets asset_manager = Assets.getInstance();
        region = asset_manager.get("textures/tilemap1.atlas",
                TextureAtlas.class).findRegion("bomb_party_v4");
    }

    protected void process(int e) {
        Box2dComponent box2dComponent = mapBox2D.get(e);
        TransformComponent transformComponent = mapTransform.get(e);
        PlayerComponent playerComponent = mapPlayer.get(e);

        // TODO: use velocity component for that?
        Body body = box2dComponent.body;
        Vector2 velocity = last_orientation.cpy().scl(playerComponent.movement_speed);
        body.setLinearVelocity(velocity);
        last_position = transformComponent.position.cpy();

        if (bombPlanted) {
            bombPlanted = false;
            if (!playerComponent.isCooledDownBomb) {
                playerComponent.isCooledDownBomb = true;
                createBomb();
            }
        }

        if (playerComponent.isCooledDownBomb) {
            float delta = world.getDelta();
            playerComponent.ttl_cooldown_bomb -= delta;
            if (playerComponent.ttl_cooldown_bomb <= 0) {
                playerComponent.isCooledDownBomb = false;
                playerComponent.ttl_cooldown_bomb = playerComponent.cooldown_bomb;
            }
        }
    }

    private int createBomb() {
        Archetype bombArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(BombComponent.class)
                .add(TimerComponent.class)
                .add(GridPositionComponent.class)
                .build(world);
        final int e = world.create(bombArchetype);
        BombComponent bombComponent = mapBomb.get(e);
        Vector3 position = last_position.cpy();
        position.z = 0;
        mapTransform.get(e).position = position;
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(region, 16, 4, 18, 6),
                6 / bombComponent.timer);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapTransform.get(e).scale = new Vector2(1f, 1f);
        playSoundDropBomb();
        return e;
    }

    private void playSoundDropBomb() {
        Assets asset_manager = Assets.getInstance();
        Sound sound = asset_manager.get("drop.wav", Sound.class);
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        audioPlayer.playSound(sound);
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
