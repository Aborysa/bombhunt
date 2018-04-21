package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.BombComponent;
import com.bombhunt.game.model.ecs.components.DestroyableComponent;
import com.bombhunt.game.model.ecs.components.ExplosionComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
import com.bombhunt.game.model.ecs.components.SolidComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.ExplosionFactory;
import com.bombhunt.game.services.assets.Assets;

public class ExplosionSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<ExplosionComponent> mapExplosion;
    private ComponentMapper<BombComponent> mapBomb;
    private ComponentMapper<DestroyableComponent> mapDestroyable;
    private ComponentMapper<KillableComponent> mapKillable;
    private ComponentMapper<SolidComponent> mapSolid;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<GridPositionComponent> mapGrid;

    private ExplosionFactory explosionFactory;
    private TextureRegion region;

    public ExplosionSystem(ExplosionFactory explosionFactory) {
        super(Aspect.all(TransformComponent.class,
                ExplosionComponent.class));
        Assets asset_manager = Assets.getInstance();
        region = asset_manager.get("textures/tilemap1.atlas",
                TextureAtlas.class).findRegion("bomb_party_v4");
        this.explosionFactory = explosionFactory;
    }

    @Override
    protected void process(int e) {
        float delta = world.getDelta();
        updateDecade(e, delta);
        updateChaining(e, delta);
        explosionDamage(e);
        playerDamage(e);
        updateDeletion(e, delta);
    }

    private void updateDecade(int e, float dt) {
        ExplosionComponent explosionComponent = mapExplosion.get(e);
        if (!explosionComponent.is_decaded) {
            explosionComponent.ttl_decade -= dt;
            if (explosionComponent.ttl_decade <= 0) {
                explosionComponent.is_decaded = true;
                decadeBomb(e);
            }
        }
    }

    private void decadeBomb(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        ExplosionComponent explosionComponent = mapExplosion.get(e);
        Grid grid = gridPositionComponent.grid;
        for (DIRECTION_ENUM direction : DIRECTION_ENUM.values()) {
            Vector3 dir = direction.getVector();
            Vector3 offset = dir.cpy().scl(grid.getCellSize());
            Vector3 prev_position = transformComponent.position.cpy();
            Vector3 position = prev_position.add(offset);
            Boolean hasSolid = grid.detect(position, mapSolid);
            if (!hasSolid) {
                int new_e = explosionFactory.createExplosion(position,
                        explosionComponent.damage, explosionComponent.range);
                ExplosionComponent new_explosionComponent = mapExplosion.get(new_e);
                new_explosionComponent.direction = direction;
                new_explosionComponent.is_decaded = true;
                new_explosionComponent.range -= 1;
            } else {
                if (destructionDamage(e, position)) {
                    explosionComponent.range = 0;
                }
            }
        }
    }

    private void updateChaining(int e, float dt) {
        ExplosionComponent explosionComponent = mapExplosion.get(e);
        if (explosionComponent.direction != null) {
            explosionComponent.ttl_decade -= dt;
            if (explosionComponent.ttl_decade <= 0) {
                if (explosionComponent.range > 0) {
                    TransformComponent transformComponent = mapTransform.get(e);
                    GridPositionComponent gridPositionComponent = mapGrid.get(e);
                    Grid grid = gridPositionComponent.grid;
                    Vector3 offset = explosionComponent.direction.getVector().cpy().scl(grid.getCellSize());
                    Vector3 prev_position = transformComponent.position.cpy();
                    Vector3 position = prev_position.add(offset);
                    Boolean hasSolid = grid.detect(position, mapSolid);
                    if (!hasSolid) {
                        int new_e = explosionFactory.createExplosion(position,
                                explosionComponent.damage, explosionComponent.range);
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
    }

    private void explosionDamage(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        Grid grid = gridPositionComponent.grid;
        IntBag bombsEntities = grid.filterEntities(transformComponent.position.cpy(), mapBomb);
        for (int i = 0; i < bombsEntities.size(); i++) {
            int bombEntity = bombsEntities.get(i);
            BombComponent bombComponent = mapBomb.get(bombEntity);
            bombComponent.ttl_timer = 0;
        }
    }

    private void playerDamage(int e) {
        TransformComponent transformComponent = mapTransform.get(e);
        ExplosionComponent explosionComponent = mapExplosion.get(e);
        GridPositionComponent gridPositionComponent = mapGrid.get(e);
        Grid grid = gridPositionComponent.grid;
        IntBag killableEntities = grid.filterEntities(transformComponent.position.cpy(), mapKillable);
        for (int i = 0; i < killableEntities.size(); i++) {
            int killableEntity = killableEntities.get(i);
            KillableComponent killableComponent = mapKillable.get(killableEntity);
            killableComponent.damage_received += explosionComponent.damage;
            killableComponent.last_hit = explosionComponent.direction;
        }
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

    private void updateDeletion(int e, float dt) {
        ExplosionComponent explosionComponent = mapExplosion.get(e);
        explosionComponent.ttl_duration -= dt;
        if (explosionComponent.ttl_duration <= 0) {
            explosionComponent.ttl_duration = explosionComponent.duration;
            world.delete(e);
        }
    }

}
