package com.bombhunt.game.view.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.bombhunt.game.services.assets.Assets;

/**
 * Created by samuel on 02/04/18.
 * references:
 * https://stackoverflow.com/questions/26429723/
 */

public class SettingsButton extends Controller {

    // TODO: change textures
    private final String DOWN_TEXTURE_PATH = "textures/settingsButtonDown.png";
    private final String UP_TEXTURE_PATH = "textures/settingsButtonUp.png";

    private ImageButton imageButton;

    public SettingsButton(int size) {
        super(size);
        ImageButton.ImageButtonStyle style = getImageButtonStyle();
        imageButton = new ImageButton(style);
    }

    public ImageButton.ImageButtonStyle getImageButtonStyle() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = getDrawableUp();
        style.imageDown = getDrawableDown();
        resizeDrawable(style.imageUp);
        resizeDrawable(style.imageDown);
        return style;
    }

    private Drawable getDrawableDown() {
        Assets assetsManager = Assets.getInstance();
        Texture texture_down = assetsManager.get(DOWN_TEXTURE_PATH, Texture.class);
        return getDrawableFromTexture(texture_down);
    }

    private Drawable getDrawableUp() {
        Assets assetsManager = Assets.getInstance();
        Texture texture_up = assetsManager.get(UP_TEXTURE_PATH, Texture.class);
        return getDrawableFromTexture(texture_up);
    }

    public ImageButton getImageButton() {
        return imageButton;
    }
}
