package com.bombhunt.game.model.ecs.factories;

import com.bombhunt.game.model.ecs.components.ItemComponent;
import com.bombhunt.game.model.ecs.components.PlayerComponent;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

public enum ITEM_TYPE_ENUM {
    HEAL(25f, 0, 200, 1, 2) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.max_health =
                    (int) max(min(playerComponent.max_health + getAmount(), getMaxAmount()), getMinAmount());
            playerComponent.malus = (int) -getAmount();
        }
    },

    POISON(-50f, 0, 0, 0, 2) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.malus = (int) -getAmount();
        }
    },
    BOMB_DAMAGE(50f, 0, 200f, 0, 0) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.bomb_damage =
                    (int) max(min(playerComponent.bomb_damage + getAmount(), getMaxAmount()), getMinAmount());
        }
    },
    BOMB_RANGE(1f, 0, Float.MAX_VALUE, 3, 4) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.bomb_range =
                    (int) max(min(playerComponent.bomb_range + getAmount(), getMaxAmount()), getMinAmount());
        }
    },
    SPEED(1f, 1f, 4f, 3, 3) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.movement_speed =
                    max(min(playerComponent.movement_speed + getAmount(), getMaxAmount()), getMinAmount());
        }
    },
    BOMB_COOLDOWN(-0.1f, 0.5f, 1f, 2, 4) {
        @Override
        public void applyItem(ItemComponent itemComponent, PlayerComponent playerComponent) {
            playerComponent.bomb_cooldown =
                    round(max(min(playerComponent.bomb_cooldown + getAmount(), getMaxAmount()), getMinAmount())*10f)/10f;
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
