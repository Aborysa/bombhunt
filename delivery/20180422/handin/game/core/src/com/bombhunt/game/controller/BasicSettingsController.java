package com.bombhunt.game.controller;

import com.bombhunt.game.BombHunt;

/**
 * Created by samuel on 05/04/18.
 */

public abstract class BasicSettingsController extends BasicController {
    protected BasicSettingsController(BombHunt bombHunt) {
        super(bombHunt);
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
