package com.bombhunt.game.controller;

import com.badlogic.gdx.math.Vector2;
import com.bombhunt.game.BombHunt;

/**
 * Created by samuel on 29/03/18.
 */

public class GameController extends BasicController {

    private static GameController instance;

    private GameController(BombHunt bombHunt) {
        super(bombHunt);
    }

    public static GameController getInstance(BombHunt bombHunt) {
        if (instance == null) {
            instance = new GameController(bombHunt);
        }
        return instance;
    }

    public void playerMove(Vector2 orientation) {
        
    }

    public void playerPlantBomb() {

    }
}
