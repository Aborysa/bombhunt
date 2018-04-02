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
 * http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=16249
 */

public class Joystick {

    private final String backgroundTexturePath = "textures/analogBackground.png";
    private final String knobTexturePath = "textures/analogForeground.png";
    private final int DEAD_ZONE_RADIUS = 10;
    private final float RATIO_KNOB = 0.5f;

    private Touchpad touchpad;
    private int size;

    public Joystick(int size) {
        this.size = size;
        Touchpad.TouchpadStyle style = getTouchpadStyle();
        touchpad = new Touchpad(DEAD_ZONE_RADIUS, style);
    }

    private Touchpad.TouchpadStyle getTouchpadStyle() {
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        style.background = getBackgroundDrawable();
        resizeBackground(style.background);
        style.knob = getKnobDrawable();
        resizeKnob(style.knob);
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

    private void resizeBackground(Drawable background) {
        background.setMinWidth(size);
        background.setMinHeight(size);
    }

    private void resizeKnob(Drawable knob) {
        knob.setMinWidth(size*RATIO_KNOB);
        knob.setMinHeight(size*RATIO_KNOB);
    }

    public Touchpad getTouchpad() {
        // TODO: TEMPORARY should be remove with addition of controller
        return touchpad;
    }

    //TODO: make the joystick bigger and make it move with finger position
}
