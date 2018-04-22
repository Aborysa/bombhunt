package com.bombhunt.game.model.ecs.systems;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by samuel on 20/04/18.
 */

public enum DIRECTION_ENUM {
    UP(0, false, new Vector3(0, 1, 0)),
    DOWN(1, false, new Vector3(0, -1, 0)),
    LEFT(4, true, new Vector3(-1, 0, 0)),
    RIGHT(4, false, new Vector3(1, 0, 0));


    private int frame;
    private boolean flip;
    private Vector3 vector;

    DIRECTION_ENUM(int frame, boolean flip, Vector3 vector) {
        this.frame = frame;
        this.flip = flip;
        this.vector = vector;
    }

    public int getFrame(){
        return frame;
    }

    public boolean isFlip() {
        return flip;
    }

    public Vector3 getVector() { return vector; }
}
