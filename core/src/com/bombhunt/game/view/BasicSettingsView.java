package com.bombhunt.game.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.controller.BasicSettingsController;

/**
 * Created by samuel on 05/04/18.
 */

public class BasicSettingsView {
    public static Slider createMusicSlider(BasicSettingsController controller, Skin skin) {
        Slider slider_music = new Slider(0, 100, 1, false, skin);
        float current_volume_music = controller.getVolumeMusic();
        slider_music.setValue(current_volume_music*100);
        slider_music.getStyle().knob.setMinHeight(100);
        ChangeListener listener_volume_music = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setVolumeMusic(slider_music.getValue()/100);
            }
        };
        slider_music.addListener(listener_volume_music);
        return slider_music;
    }

    public static Slider createSoundFXSlider(BasicSettingsController controller, Skin skin) {
        Slider slider_soundFX = new Slider(0, 100, 1, false, skin);
        float current_volume_sound = controller.getVolumeSound();
        slider_soundFX.setValue(current_volume_sound*100);
        slider_soundFX.getStyle().knob.setMinHeight(100);
        ChangeListener listener_volume_sound = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setVolumeSoundFX(slider_soundFX.getValue()/100);
            }
        };
        slider_soundFX.addListener(listener_volume_sound);
        return slider_soundFX;
    }
}
