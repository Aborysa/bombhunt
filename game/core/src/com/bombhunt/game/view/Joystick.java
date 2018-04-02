package com.bombhunt.game.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.bombhunt.game.services.assets.Assets;

/**
 * Created by bartc on 3/27/2018.
 */

public class Joystick extends Touchpad {
    private static Touchpad.TouchpadStyle style;
    private static Skin skin;
    private static Drawable background;
    private static Drawable knob;

    public Joystick(float x, float y) {
        super(10, getTouchpadStyle());
        setBounds(15, 15, 600, 600);
        setPosition(x, y);
    }

    private static Touchpad.TouchpadStyle getTouchpadStyle() {
        skin = new Skin();
        skin.add("background", Assets.getInstance().get("textures/analogBackground.png", Texture.class));
        skin.add("knob", Assets.getInstance().get("textures/analogForeground.png", Texture.class));
        style = new Touchpad.TouchpadStyle();
        background = skin.getDrawable("background");
        knob = skin.getDrawable("knob");
        style.background = background;
        style.knob = knob;
        return style;
    }

    //TODO: make the joystick bigger and make it move with finger position
}
