package com.bombhunt.game.model.ecs.systems;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.DestroyableComponent;
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.SolidComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TimerComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.graphics.SpriteHelper;

public class ExplosionSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<ExplosionComponent> mapExplosion;
    private ComponentMapper<TimerComponent> mapTimer;
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<DestroyableComponent> mapDestroyable;
    private ComponentMapper<SolidComponent> mapSolid;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<GridPositionComponent> mapGrid;

    private TextureRegion region;

    public ExplosionSystem() {
        super(Aspect.all(TransformComponent.class,
                ExplosionComponent.class,
                TimerComponent.class));
        Assets asset_manager = Assets.getInstance();
        region = asset_manager.get("textures/tilemap1.atlas",
                TextureAtlas.class).findRegion("bomb_party_v4");
    }

    @Override
    protected void process(int e) {
        ExplosionComponent explosionComponent = mapExplosion.get(e);
        float delta = world.getDelta();

        if (!explosionComponent.is_decaded) {
            explosionComponent.ttl_decade -= delta;
            if (explosionComponent.ttl_decade <= 0) {
                explosionComponent.is_decaded = true;
                decadeBomb(e);
            }
        }

        if (explosionComponent.direction != null) {
            explosionComponent.ttl_decade -= delta;
            if (explosionComponent.ttl_decade <= 0) {
                if (explosionComponent.range > 0) {
                    TransformComponent transformComponent = mapTransform.get(e);
                    GridPositionComponent gridPositionComponent = mapGrid.get(e);
                    Grid grid = gridPositionComponent.grid;
                    Vector3 offset = explosionComponent.direction.cpy().scl(grid.getCellSize());
                    Vector3 prev_position = transformComponent.position;
                    Vector3 position = prev_position.cpy().add(offset);
                    Boolean hasSolid = grid.detect(position, mapSolid);
                    if (!hasSolid) {
                        int new_e = createExplosion(position);
                        ExplosionComponent new_explosionComponent = mapExplosion.get(new_e);
                        new_explosionComponent.direction = explosionComponent.direction;
                        new_explosionComponent.is_decaded = true;
                        new_explosionComponent.range = explosionComponent.range - 1;
                        explosionComponent.range = 0;
                    } else {
                        if (destructionDamage(e, position)) {
                            explosionComponent.range = 0;
                        }
                    }
                }
            }
        }

        explosionDamage(e);

        explosionComponent.duration -= delta;
        if (explosionComponent.duration <= 0) {
            world.delete(e);
        }
    }

    private void decadeBomb(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        Grid grid = gridPositionComponent.grid;
        Vector3[] dirs = {new Vector3(0, 1, 0),
                new Vector3(1, 0, 0),
                new Vector3(0, -1, 0),
                new Vector3(-1, 0, 0)};
        for (Vector3 dir : dirs) {
            Vector3 offset = dir.cpy().scl(grid.getCellSize());
            Vector3 prev_position = transformComponent.position;
            Vector3 position = prev_position.cpy().add(offset);
            Boolean hasSolid = grid.detect(position, mapSolid);
            if (!hasSolid) {
                int new_e = createExplosion(position);
                ExplosionComponent new_explosionComponent = mapExplosion.get(new_e);
                new_explosionComponent.direction = dir;
                new_explosionComponent.is_decaded = true;
            } else {
                if (destructionDamage(e, position)) {
                    ExplosionComponent explosionComponent = mapExplosion.get(e);
                    explosionComponent.range = 0;
                }
            }
        }
    }

    private int createExplosion(Vector3 position){
        Archetype explosionArchetype = new ArchetypeBuilder()
                .add(TransformComponent.class)
                .add(SpriteComponent.class)
                .add(AnimationComponent.class)
                .add(ExplosionComponent.class)
                .add(TimerComponent.class)
                .add(GridPositionComponent.class)
                .build(world);
        int e = world.create(explosionArchetype);
        ExplosionComponent explosionComponent = mapExplosion.get(e);
        float duration = explosionComponent.duration;
        mapTransform.get(e).position = position;
        mapTransform.get(e).rotation = 90f * (float) Math.random();
        mapAnimation.get(e).animation = SpriteHelper.createDecalAnimation(
                SpriteHelper.createSprites(region, 16, 4, 13, 3),
                3 / duration);
        mapSprite.get(e).sprite = mapAnimation.get(e).animation.getKeyFrame(0, true);
        mapTransform.get(e).scale = new Vector2(1f, 1f);
        return e;
    }

    private void explosionDamage(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        Grid grid = gridPositionComponent.grid;
        IntBag bombsEntities = grid.filterEntities(transformComponent.position, mapBomb);
        for (int i = 0; i < bombsEntities.size(); i++) {
            int bombEntity = bombsEntities.get(i);
            BombComponent bombComponent = mapBomb.get(bombEntity);
            bombComponent.timer = 0;
        }
        //TODO else if (hasHealth) {health -= damage}
    }

    private Boolean destructionDamage(int e, Vector3 position) {
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        Grid grid = gridPositionComponent.grid;
        IntBag destroyableEntities = grid.filterEntities(position, mapDestroyable);
        for (int i = 0; i < destroyableEntities.size(); i++) {
            int destroyableEntity = destroyableEntities.get(i);
            mapDestroyable.get(destroyableEntity).health -= 1;
        }
        return destroyableEntities.size() != 0;
    }

}
