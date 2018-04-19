package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.AnimationComponent;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.ItemComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.services.assets.Assets;

public class ItemSystem extends IteratingSystem {

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<ItemComponent> mapItem;
    private ComponentMapper<AnimationComponent> mapAnimation;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<GridPositionComponent> mapGrid;
    private ComponentMapper<PlayerComponent> mapPlayer;
    private ComponentMapper<KillableComponent> mapKillable;

    private TextureRegion region;

    public ItemSystem() {
        super(Aspect.all(TransformComponent.class,
                ItemComponent.class,
                GridPositionComponent.class));
        Assets asset_manager = Assets.getInstance();
    }


    @Override
    protected void process(int e) {
        float dt = world.getDelta();
        ItemComponent itemComponent = mapItem.get(e);
        SpriteComponent spriteComponent = mapSprite.get(e);
        itemComponent.timeout -= dt;
        if (itemComponent.timeout < 0) {
            world.delete(e);
        } else {
            if (itemComponent.timeout < itemComponent.flickerTime) {
                int divide = (int) (itemComponent.timeout / 0.2f) % 2;
                if (divide == 0) {
                    spriteComponent.sprite.setColor(1, 1, 1, 0f);
                } else {
                    spriteComponent.sprite.setColor(1, 1, 1, 1f);
                }
            }
            TransformComponent transformComponent = mapTransform.get(e);
            GridPositionComponent gridPositionComponent = mapGrid.get(e);
            Grid grid = gridPositionComponent.grid;
            IntBag playerEntities = grid.filterEntities(transformComponent.position, mapPlayer);
            for (int i = 0; i < playerEntities.size(); i++) {
                int playerEntity = playerEntities.get(i);

                applyItem(itemComponent, playerEntity);
                world.delete(e);
                break;
            }
        }

    }

    private void applyItem(ItemComponent item, int playerEntity) {
        PlayerComponent playerComponent = mapPlayer.get(playerEntity);
        switch (item.type) {
            case INCREASEDAMAGE:
                playerComponent.bomb_damage =
                        Math.min(playerComponent.bomb_damage + item.type.getAmount(), item.type.getMaxAmount());
                break;
            case INCREASEHEALTH:
                playerComponent.max_health =
                        Math.min(playerComponent.max_health + item.type.getAmount(), item.type.getMaxAmount());
                KillableComponent killableComponent = mapKillable.get(playerEntity);
                killableComponent.health = Math.min(killableComponent.health + item.type.getAmount(), playerComponent.max_health);
                break;
            case INCREASERANGE:
                playerComponent.bomb_range =
                        Math.min(playerComponent.bomb_range + item.type.getAmount(), item.type.getMaxAmount());
                break;
            case INCREASESPEED:
                playerComponent.movement_speed =
                        Math.min(playerComponent.movement_speed + (item.type.getMaxAmount() - playerComponent.movement_speed) / 4,
                                item.type.getMaxAmount());
                break;


        }
    }
}
