package com.bombhunt.game.model.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.bombhunt.game.model.Grid;
import com.bombhunt.game.model.ecs.components.GridPositionComponent;
import com.bombhunt.game.model.ecs.components.ItemComponent;
import com.bombhunt.game.model.ecs.components.KillableComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;
import com.bombhunt.game.model.ecs.components.SpriteComponent;
import com.bombhunt.game.model.ecs.components.TransformComponent;
import com.bombhunt.game.model.ecs.factories.ITEM_TYPE_ENUM;

public class ItemSystem extends IteratingSystem {

    private ComponentMapper<TransformComponent> mapTransform;
    private ComponentMapper<GridPositionComponent> mapGrid;
    private ComponentMapper<SpriteComponent> mapSprite;
    private ComponentMapper<ItemComponent> mapItem;
    private ComponentMapper<PlayerComponent> mapPlayer;
    private ComponentMapper<KillableComponent> mapKillable;

    public ItemSystem() {
        super(Aspect.all(TransformComponent.class,
                GridPositionComponent.class,
                ItemComponent.class));
    }

    @Override
    protected void process(int e) {
        float dt = world.getDelta();
        ItemComponent itemComponent = mapItem.get(e);
        itemComponent.ttl_timer -= dt;
        if (itemComponent.ttl_timer < 0) {
            world.delete(e);
        } else {
            SpriteComponent spriteComponent = mapSprite.get(e);
            updateBlinking(itemComponent, spriteComponent);
            TransformComponent transformComponent = mapTransform.get(e);
            GridPositionComponent gridPositionComponent = mapGrid.get(e);
            Grid grid = gridPositionComponent.grid;
            IntBag playerEntities = grid.filterEntities(transformComponent.position, mapKillable);
            for (int i = 0; i < playerEntities.size(); i++) {
                
                int playerEntity = playerEntities.get(i);
                if(mapPlayer.has(playerEntity)){
                    PlayerComponent playerComponent = mapPlayer.get(playerEntity);
                    itemComponent.type.applyItem(itemComponent, playerComponent);
                }
                world.delete(e);
                // break;
            }
        }
    }

    private void updateBlinking(ItemComponent itemComponent, SpriteComponent spriteComponent) {
        if (itemComponent.ttl_timer < itemComponent.blinkingTime) {
            float alpha = (int) (itemComponent.ttl_timer / 0.2f) % 2;
            spriteComponent.sprite.setColor(1, 1, 1, alpha);
        }
    }
}
