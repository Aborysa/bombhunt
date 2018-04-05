package com.bombhunt.game.view;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by samuel on 05/04/18.
 */

public class InGameMenu extends Dialog {
    public InGameMenu(String title, Skin skin) {
        super(title, skin);

        text("Here is the in game menu");
        button("ok", true);
    }


}
