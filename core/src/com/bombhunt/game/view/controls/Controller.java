package com.bombhunt.game.view.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by samuel on 02/04/18.
 */

public class Controller {
    protected int size;

    public Controller(int size){
        this.size = size;
    }

    protected void resizeDrawable(Drawable drawable) {
        drawable.setMinWidth(size);
        drawable.setMinHeight(size);
    }

    protected void resizeDrawable(Drawable drawable, float ratio) {
        drawable.setMinWidth(size*ratio);
        drawable.setMinHeight(size*ratio);
    }

    protected Drawable getDrawableFromTexture(Texture texture) {
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
