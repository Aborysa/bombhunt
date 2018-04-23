package com.bombhunt.game.controller;

import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.screens.MainMenuScreen;

/**
 * Created by samuel on 29/03/18.
 */

public class InGameSettingsController extends BasicSettingsController {

    public InGameSettingsController(BombHunt bombHunt) {
        super(bombHunt);
    }

    public void backToMainMenu() {changeView(new MainMenuScreen(bombHunt));}

}
