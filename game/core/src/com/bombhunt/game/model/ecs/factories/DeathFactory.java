package com.bombhunt.game.model.ecs.factories;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.DeathComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.ItemComponent;
import com.bombhunt.game.model.ecs.components.NetworkComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.systems.DIRECTION_ENUM;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.audio.AudioPlayer;
import com.bombhunt.game.services.graphics.SpriteHelper;

import java.util.Random;

/**
 * Created by samuel on 19.04.2018.
 */

public class DeathFactory implements IEntityFactory {

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<GridPositionComponent> mapGrid;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<DeathComponent> mapDeath;

    private World world;
    private Grid grid;
    private Archetype deathArchetype;

    @Override
    public void setWorld(World world) {
        this.world = world;

        mapTransform = world.getMapper(TransformComponent.class);
        mapGrid = world.getMapper(GridPositionComponent.class);
        mapSprite = world.getMapper(SpriteComponent.class);
        mapAnimation = world.getMapper(AnimationComponent.class);
        mapDeath = world.getMapper(DeathComponent.class);

        deathArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(GridPositionComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(DeathComponent.class)
                .build(world);
    }

    @Override
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public int createDeath(Vector3 position, Sprite sprite, DIRECTION_ENUM last_hit) {
        final int e = world.create(deathArchetype);
        mapTransform.get(e).position = position;
        mapTransform.get(e).scale = new Vector2(1f, 1f);
        mapTransform.get(e).rotation = last_hit == DIRECTION_ENUM.LEFT ? 90 : -90;
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        gridPositionComponent.grid = grid;
        gridPositionComponent.cellIndex = gridPositionComponent.grid.getCellIndex(position);
        Array<Sprite> new_array_animation = new Array<>();
        new_array_animation.add(sprite);
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(new_array_animation, 60);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        playEvilLaughSound();
        return e;
    }

    private void playEvilLaughSound(){
        Assets asset_manager = Assets.getInstance();
        Sound evil_sound = asset_manager.get("evilLaugh.ogg", Sound.class);
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        audioPlayer.playSound(evil_sound);
    }
}
