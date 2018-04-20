package com.bombhunt.game.model.ecs.systems;

/**
 * Created by samuel on 20/04/18.
 */

public enum DIRECTION_ENUM {
    UP(0, false),
    DOWN(1, false),
    LEFT(4, true),
    RIGHT(4, false);

    private final int frame;
    private final boolean flip;

    DIRECTION_ENUM(int frame, boolean flip) {
        this.frame = frame;
        this.flip = flip;
    }

    public int getFrame(){
        return frame;
    }

    public boolean isFlip() {
        return flip;
    }
}
