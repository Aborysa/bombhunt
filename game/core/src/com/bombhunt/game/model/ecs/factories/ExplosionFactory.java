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
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.graphics.SpriteHelper;

/**
 * Created by erlin on 27.03.2018.
 */

public class ExplosionFactory implements IEntityFactory {

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<GridPositionComponent> mapGrid;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<ExplosionComponent> mapExplosion;

    private World world;
    private Grid grid;
    private Archetype explosionArchetype;
    private TextureRegion region;

    public ExplosionFactory() {
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
        mapAnimation = world.getMapper(AnimationComponent.class);
        mapExplosion = world.getMapper(ExplosionComponent.class);

        explosionArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(GridPositionComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(ExplosionComponent.class)
                .build(world);
    }

    @Override
    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public int createExplosion(Vector3 position) {
        int e = world.create(explosionArchetype);
        ExplosionComponent explosionComponent = mapExplosion.get(e);
        float duration = explosionComponent.duration;
        mapTransform.get(e).position = position;
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        gridPositionComponent.grid = grid;
        gridPositionComponent.cellIndex = gridPositionComponent.grid.getCellIndex(position);
        mapTransform.get(e).rotation = 90f * (float) Math.random();
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(region, 16, 4, 13, 3),
                3 / duration);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapTransform.get(e).scale = new Vector2(1f, 1f);
        return e;
    }

}
