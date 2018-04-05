package com.bombhunt.game.controller;

import com.bombhunt.game.BombHunt;

/**
 * Created by samuel on 29/03/18.
 */

public class SettingsController extends BasicSettingsController {

    private static SettingsController instance;

    private SettingsController(BombHunt bombHunt) {
        super(bombHunt);
    }

    public static SettingsController getInstance(BombHunt bombHunt) {
        if (instance == null) {
            instance = new SettingsController(bombHunt);
        }
        return instance;
    }

}
