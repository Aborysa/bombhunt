package com.bombhunt.game.controller;

import com.bombhunt.game.BombHunt;

/**
 * Created by samuel on 29/03/18.
 */

public class CreditsController extends BasicController {

    private static CreditsController instance;

    private CreditsController(BombHunt bombHunt) {
        super(bombHunt);
    }

    public static CreditsController getInstance(BombHunt bombHunt) {
        if (instance == null) {
            instance = new CreditsController(bombHunt);
        }
        return instance;
    }
}
