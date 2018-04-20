package com.bombhunt.game.model.ecs.factories;

import com.bombhunt.game.model.ecs.components.ItemComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;

// TODO: add item for bomb cooldown
public enum ITEM_TYPE_ENUM {
    HEALTH(25, 200, 1, 2) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.max_health =
                    Math.min(playerComponent.max_health + getAmount(), getMaxAmount());
            playerComponent.health = Math.min(playerComponent.health + getAmount(), playerComponent.max_health);
        }
    },
    DAMAGE(50, 200, 0, 0) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.bomb_damage =
                    Math.min(playerComponent.bomb_damage + getAmount(), getMaxAmount());
        }
    },
    RANGE(1, Integer.MAX_VALUE, 4, 4) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.bomb_range =
                    Math.min(playerComponent.bomb_range + getAmount(), getMaxAmount());
        }
    },
    SPEED(1, 4, 3, 3) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.movement_speed =
                    Math.min(playerComponent.movement_speed + getAmount(), getMaxAmount());
        }
    };

    private final int amount;
    private final int maxAmount;
    private final int coord_x;
    private final int coord_y;

    ITEM_TYPE_ENUM(int amount, int maxAmount, int coord_x, int coord_y) {
        this.amount = amount;
        this.maxAmount = maxAmount;
        this.coord_x = coord_x;
        this.coord_y = coord_y;
    }

    public int getAmount() {
        return amount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getCoord_x() {return coord_x;}

    public int getCoord_y() {return coord_y;}

    public abstract void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent);
}
