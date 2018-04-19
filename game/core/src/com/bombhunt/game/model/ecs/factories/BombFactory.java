package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.NetworkComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.graphics.SpriteHelper;

/**
 * Created by erlin on 27.03.2018.
 */

public class BombFactory implements IEntityFactory, INetworkFactory {

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<GridPositionComponent> mapGrid;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<NetworkComponent> mapNetwork;

    private World world;
    private Grid grid;
    private Archetype bombArchetype;
    private TextureRegion region;

    public BombFactory() {
        Assets asset_manager = Assets.getInstance();
        region = asset_manager.get("textures/tilemap1.atlas",
                TextureAtlas.class).findRegion("bomb_party_v4");
    }

    @Override
    public void setWorld(World world) {
        this.world = world;

        mapTransform = world.getMapper(TransformComponent.class);
        mapGrid = world.getMapper(GridPositionComponent.class);
        mapSprite = world.getMapper(SpriteComponent.class);
        mapBomb = world.getMapper(BombComponent.class);
        mapAnimation = world.getMapper(AnimationComponent.class);
        mapNetwork = world.getMapper(NetworkComponent.class);

        bombArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(GridPositionComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(BombComponent.class)
                .add(NetworkComponent.class)
                .build(world);
    }

    @Override
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    @Override
    public int createFromMessage(String message) {
        int e = createBomb(Vector3.Zero);

        // TODO: to be adapted by network pull request after rebase
//        NetworkComponent networkComponent = mapNetwork.get(e);
//        message.getNetwork(networkComponent);
//        message.getTransform(mapTransform.get(e));
//        message.getTimer(mapTimer.get(e));
//        networkComponent.owner = message.getSender();

        // TODO: THINK WE DONT HAVE TO PLAY THE SOUND
        // purpose of sound was for user to know that a bomb just been planted
        // playSoundDropBomb();
        return e;
    }

    // TODO: delete if not used
//    private void playSoundDropBomb() {
//        Assets asset_manager = Assets.getInstance();
//        Sound sound = asset_manager.get("drop.wav", Sound.class);
//        AudioPlayer audioPlayer = AudioPlayer.getInstance();
//        audioPlayer.playSound(sound);
//    }

    private int createBomb(Vector3 position) {
        final int e = world.create(bombArchetype);
        BombComponent bombComponent = mapBomb.get(e);
        mapTransform.get(e).position = position;
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        gridPositionComponent.grid = grid;
        //gridPositionComponent.cellIndex = gridPositionComponent.grid.getCellIndex(position);
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(region, 16, 4, 18, 6),
                6 / bombComponent.timer);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapTransform.get(e).scale = new Vector2(1f, 1f);
        return e;
    }

    public int createBomb(Vector3 position, int damage, int range) {
        int e = createBomb(position);
        BombComponent bombComponent = mapBomb.get(e);
        bombComponent.damage = damage;
        bombComponent.range = range;
        return e;
    }
}
