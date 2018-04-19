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
                int divide = (int)(itemComponent.timeout / 0.2f) % 2;
                if (divide == 0) {
                    spriteComponent.sprite.setColor(1,1,1,0f);
                } else {
                    spriteComponent.sprite.setColor(1,1,1,1f);
                }
            }
            TransformComponent transformComponent = mapTransform.get(e);
            GridPositionComponent gridPositionComponent = mapGrid.get(e);
            Grid grid = gridPositionComponent.grid;
            IntBag playerEntities = grid.filterEntities(transformComponent.position, mapPlayer);
            for (int i = 0; i < playerEntities.size(); i++) {
                int playerEntity = playerEntities.get(i);
                PlayerComponent playerComponent = mapPlayer.get(playerEntity);
                applyItem(itemComponent, playerComponent);
                world.delete(e);
                break;
            }
        }

    }

    private void applyItem(ItemComponent item, PlayerComponent player) {
        switch(item.type) {
            case INCREASEDAMAGE: player.bomb_damage =
                    Math.max(player.bomb_damage+item.type.getAmount(), item.type.getMaxAmount());
                                 break;
            case INCREASEHEALTH: player.max_health =
                    Math.max(player.max_health+item.type.getAmount(), item.type.getMaxAmount());
                                 break;
            case INCREASERANGE:  player.bomb_range =
                    Math.max(player.bomb_range+item.type.getAmount(), item.type.getMaxAmount());
                                 break;
            case INCREASESPEED:  player.movement_speed =
                    Math.max(player.movement_speed+item.type.getAmount(), item.type.getMaxAmount());
                break;


        }
    }
}
