package com.bombhunt.game.model.ecs.factories;

import com.bombhunt.game.model.ecs.components.ItemComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;

import static java.lang.Math.max;
import static java.lang.Math.min;

public enum ITEM_TYPE_ENUM {
    HEALTH(25f, 0, 200f, 1, 2) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.max_health =
                    (int) max(min(playerComponent.max_health + getAmount(), getMaxAmount()), getMinAmount());
            playerComponent.health = (int) min(playerComponent.health + getAmount(), playerComponent.max_health);
        }
    },
    POISON(-50f, 0, 200f, 0, 2) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.health =
                    (int) min(playerComponent.health + getAmount(), playerComponent.max_health);
        }
    },
    DAMAGE(50f, 0, 200f, 0, 0) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.bomb_damage =
                    (int) max(min(playerComponent.bomb_damage + getAmount(), getMaxAmount()), getMinAmount());
        }
    },
    RANGE(2f, 0, Float.MAX_VALUE, 4, 4) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.bomb_range =
                    (int) max(min(playerComponent.bomb_range * getAmount(), getMaxAmount()), getMinAmount());
        }
    },
    SPEED(0.5f, 1f, 4f, 3, 3) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.movement_speed =
                    max(min(playerComponent.movement_speed + getAmount(), getMaxAmount()), getMinAmount());
        }
    },
    BOMB_COOLDOWN(-0.1f, 0.5f, 1f, 2, 4) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.cooldown_bomb =
                    max(min(playerComponent.cooldown_bomb + getAmount(), getMaxAmount()), getMinAmount());
        }
    };

    private final float amount;
    private final float minAmount;
    private final float maxAmount;
    private final int coord_x;
    private final int coord_y;

    ITEM_TYPE_ENUM(float amount, float minAmount, float maxAmount, int coord_x, int coord_y) {
        this.amount = amount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.coord_x = coord_x;
        this.coord_y = coord_y;
    }

    public float getAmount() {
        return amount;
    }

    public float getMinAmount() {
        return minAmount;
    }

    public float getMaxAmount() {
        return maxAmount;
    }

    public int getCoord_x() {
        return coord_x;
    }

    public int getCoord_y() {
        return coord_y;
    }

    public abstract void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent);
}
