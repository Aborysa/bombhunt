package com.bombhunt.game.controller;

import com.bombhunt.game.BombHunt;

/**
 * Created by samuel on 29/03/18.
 */

public class SettingsController extends BasicController {

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
