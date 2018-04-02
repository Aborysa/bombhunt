package com.bombhunt.game.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombhunt.game.services.assets.Assets;

/**
 * Created by bartc on 3/27/2018.
 * references:
 * https://stackoverflow.com/questions/32145428/
 */

public class Joystick {

    private final String backgroundTexturePath = "textures/analogBackground.png";
    private final String knobTexturePath = "textures/analogForeground.png";
    private final int DEAD_ZONE_RADIUS = 10;
    private final int DEFAULT_X = 15;
    private final int DEFAULT_Y = 15;
    private final int SIZE = 600;

    private Touchpad touchpad;

    public Joystick(float x, float y) {
        touchpad = new Touchpad(DEAD_ZONE_RADIUS, getTouchpadStyle());
        touchpad.setBounds(DEFAULT_X, DEFAULT_Y, SIZE, SIZE);
        touchpad.setPosition(x, y);
    }

    private Touchpad.TouchpadStyle getTouchpadStyle() {
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        style.background = getBackgroundDrawable();
        style.knob = getKnobDrawable();
        return style;
    }

    private Drawable getBackgroundDrawable() {
        Assets assetsManager = Assets.getInstance();
        Texture backgroundTexture = assetsManager.get(backgroundTexturePath, Texture.class);
        return getDrawableFromTexture(backgroundTexture);
    }

    private Drawable getKnobDrawable() {
        Assets assetsManager = Assets.getInstance();
        Texture knobTexture = assetsManager.get(knobTexturePath, Texture.class);
        return getDrawableFromTexture(knobTexture);
    }

    private Drawable getDrawableFromTexture(Texture texture) {
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public Touchpad getTouchpad() {
        // TODO: TEMPORARY should be remove with addition of controller
        return touchpad;
    }

    //TODO: make the joystick bigger and make it move with finger position
}
