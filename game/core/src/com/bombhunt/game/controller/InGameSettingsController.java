package com.bombhunt.game.controller;

import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.screens.MainMenuScreen;

/**
 * Created by samuel on 29/03/18.
 */

public class InGameSettingsController extends BasicSettingsController {

    private static InGameSettingsController instance;

    private InGameSettingsController(BombHunt bombHunt) {
        super(bombHunt);
    }

    public static InGameSettingsController getInstance(BombHunt bombHunt) {
        if (instance == null) {
            instance = new InGameSettingsController(bombHunt);
        }
        return instance;
    }

    public void backToMainMenu() {changeView(new MainMenuScreen(bombHunt));}

}
