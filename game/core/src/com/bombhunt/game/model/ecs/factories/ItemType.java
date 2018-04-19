package com.bombhunt.game.model.ecs.factories;

public enum ItemType {
    INCREASERANGE(1, Integer.MAX_VALUE), INCREASEDAMAGE(50, 200), INCREASEHEALTH(25, 200),
    INCREASESPEED(1, 5);

    private final int amount;
    private final int maxAmount;
    ItemType(int amount, int maxAmount) {
        this.amount = amount;
        this.maxAmount = maxAmount;
        }
    public int getAmount() { return amount; }
    public int getMaxAmount() { return maxAmount; }
}
