package com.bombhunt.game.controller;

import com.badlogic.gdx.math.Vector2;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.model.ecs.systems.PlayerSystem;
import com.bombhunt.game.view.screens.MainMenuScreen;

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

    public void backToMainMenu() {changeView(new MainMenuScreen(bombHunt));}

    // TODO: DUPLICATE CODE BETWEEN SETTING SCREEN AND IN GAME SETTING
    // TODO: should find a way to combine functionnalities together
    public float getVolumeMusic() {
        return bombHunt.audioPlayer.getVolumeThemeSong();
    }

    public float getVolumeSound() {
        return bombHunt.audioPlayer.getVolumeSoundFX();
    }

    public void setVolumeMusic(float volume) {
        bombHunt.audioPlayer.setVolumeThemeSong(volume);
    }

    public void setVolumeSoundFX(float volume) {
        bombHunt.audioPlayer.setVolumeSoundFX(volume);
    }

}
