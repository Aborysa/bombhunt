package com.bombhunt.game.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by bartc on 3/27/2018.
 */


public class Joystick extends Touchpad {

    private static Touchpad.TouchpadStyle touchpadStyle;
    private static Skin touchpadSkin;
    private static Drawable touchBackground;
    private static Drawable touchKnob;

    public Joystick(float x, float y) {

        super(10, getTouchpadStyle());
        setBounds(15, 15, 300, 300);
        setPosition(x,y);

    }

    private static Touchpad.TouchpadStyle getTouchpadStyle() {

        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", Assets.getInstance().get("textures/analogBackground.png", Texture.class));

        touchpadSkin.add("touchKnob", Assets.getInstance().get("textures/analogForeground.png", Texture.class));

        touchpadStyle = new Touchpad.TouchpadStyle();

        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        touchKnob.setMinHeight(120);
        touchKnob.setMinWidth(120);

        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        return touchpadStyle;
    }
}
