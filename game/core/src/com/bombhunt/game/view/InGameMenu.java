package com.bombhunt.game.view;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.controller.GameController;

/**
 * Created by samuel on 05/04/18.
 * references:
 *
 */

public class InGameMenu extends Dialog {

    private GameController controller;
    private Skin skin;

    public InGameMenu(String title, Skin skin, GameController controller) {
        super(title, skin);
        this.controller = controller;
        this.skin = skin;
        Slider slider_music = createMusicSlider();
        Label label_slider_music = new Label("Music", skin, "default");
        Table buttonTable = getButtonTable();
        buttonTable.add(label_slider_music);
        buttonTable.add(slider_music);
    }


    private Slider createMusicSlider() {
        Slider slider_music = new Slider(0, 100, 1, false, skin);
        float current_volume_music = controller.getVolumeMusic();
        slider_music.setValue(current_volume_music*100);
        slider_music.getStyle().knob.setMinHeight(100);
        Runnable runnable_music = new Runnable() {
            @Override
            public void run() {
                controller.setVolumeMusic(slider_music.getValue()/100);
            }
        };
        ChangeListener listener_volume_music = controller.createChangeListener(runnable_music);
        slider_music.addListener(listener_volume_music);
        return slider_music;
    }

}
