package com.bombhunt.game.view;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by samuel on 27/03/18.
 * reference: https://stackoverflow.com/questions/19857861/
 */

public enum Colors {
    GREY(142, 142, 147, 1),
    RED(255, 59, 48, 1),
    GREEN(76, 217, 100, 1),
    PURPLE(88, 86, 214, 1),
    LIGHTBLUE(52, 170, 220, 1);

    private final int r;
    private final int g;
    private final int b;
    private final int a;
    private final String rgba;

    Colors(final int r, final int g, final int b, final int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.rgba = r + ", " + g + ", " + b + ", " + a;
    }

    public Color getColor() {
        return new Color(r, g, b, a);
    }

}