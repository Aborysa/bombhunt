package com.bombhunt.game.view.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.bombhunt.game.services.assets.Assets;

/**
 * Created by bartc on 3/27/2018.
 * references:
 * https://stackoverflow.com/questions/32145428/
 * http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=16249
 */

public class Joystick extends Controller {

    private final String BACKGROUND_TEXTURE_PATH = "textures/analogBackground.png";
    private final String KNOB_TEXTURE_PATH = "textures/analogForeground.png";
    private final int DEAD_ZONE_RADIUS = 10;
    private final float KNOB_RATIO = 0.5f;

    private Touchpad touchpad;

    public Joystick(int size) {
        super(size);
        Touchpad.TouchpadStyle style = getTouchpadStyle();
        touchpad = new Touchpad(DEAD_ZONE_RADIUS, style);
    }

    private Touchpad.TouchpadStyle getTouchpadStyle() {
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        style.background = getDrawableBackground();
        resizeDrawable(style.background);
        style.knob = getDrawableKnob();
        resizeDrawable(style.knob, KNOB_RATIO);
        return style;
    }

    private Drawable getDrawableBackground() {
        Assets assetsManager = Assets.getInstance();
        Texture backgroundTexture = assetsManager.get(BACKGROUND_TEXTURE_PATH, Texture.class);
        return getDrawableFromTexture(backgroundTexture);
    }

    private Drawable getDrawableKnob() {
        Assets assetsManager = Assets.getInstance();
        Texture knobTexture = assetsManager.get(KNOB_TEXTURE_PATH, Texture.class);
        return getDrawableFromTexture(knobTexture);
    }

    public Touchpad getTouchpad() {
        return touchpad;
    }

}
