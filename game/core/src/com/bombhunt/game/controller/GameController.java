package com.bombhunt.game.controller;

import com.badlogic.gdx.math.Vector2;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.model.ecs.systems.PlayerSystem;
import com.bombhunt.game.view.screens.MainMenuScreen;

/**
 * Created by samuel on 29/03/18.
 */

public class GameController extends BasicController {

    private PlayerSystem playerSystem;

    public GameController(BombHunt bombHunt, PlayerSystem playerSystem) {
        super(bombHunt);
        this.playerSystem = playerSystem;
    }

    public void playerMove(Vector2 orientation) {
        playerSystem.move(orientation);
    }

    public void playerPlantBomb() {
        playerSystem.plantBomb();
    }

}
