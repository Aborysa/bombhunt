package com.bombhunt.game.controller;

import com.badlogic.gdx.math.Vector2;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.model.ecs.systems.PlayerSystem;

/**
 * Created by samuel on 29/03/18.
 */

public class GameController extends BasicController {

    private static GameController instance;
    private PlayerSystem playerSystem;

    private GameController(BombHunt bombHunt, PlayerSystem playerSystem) {
        super(bombHunt);
        this.playerSystem = playerSystem;
    }

    public static GameController getInstance(BombHunt bombHunt, PlayerSystem playerSystem) {
        if (instance == null) {
            instance = new GameController(bombHunt, playerSystem);
        }
        return instance;
    }

    public void playerMove(Vector2 orientation) {
        playerSystem.move(orientation);
    }

    public void playerPlantBomb() {
        playerSystem.plantBomb();
    }

}
