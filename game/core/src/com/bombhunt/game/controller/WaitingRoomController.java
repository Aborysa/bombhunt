package com.bombhunt.game.controller;

import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.screens.GameScreen;
import com.bombhunt.game.view.screens.MainMenuScreen;

/**
 * Created by erlin on 15.04.2018.
 */

public class WaitingRoomController extends BasicController {
    public WaitingRoomController(BombHunt bombHunt){ super(bombHunt); }

    public void backToMainMenu() {changeView(new MainMenuScreen(bombHunt));}
    public void enterGameScreen() {changeView(new GameScreen(bombHunt));}
}
