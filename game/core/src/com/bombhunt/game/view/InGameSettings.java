package com.bombhunt.game.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.controller.InGameSettingsController;

/**
 * Created by samuel on 05/04/18.
 * references:
 * https://www.youtube.com/watch?v=fxkuHa9FmGw
 * https://stackoverflow.com/questions/29771114
 * http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=22867
 */

public class InGameSettings extends Dialog {

    private final int PADDING_BUTTON_TABLE = 50;

    private InGameSettingsController controller;
    private Skin skin;

    public InGameSettings(String title, Skin skin, BombHunt bombHunt) {
        super(title, skin);
        this.controller = InGameSettingsController.getInstance(bombHunt);
        this.skin = skin;

        Label label_slider_music = new Label("Music", skin, "default");
        Slider slider_music = BasicSettingsView.createMusicSlider(controller, skin);

        Label label_slider_sounds  = new Label("Sound FX", skin, "default");
        Slider slider_sounds = BasicSettingsView.createSoundFXSlider(controller, skin);

        TextButton leaveButton = new TextButton("Leave Game", skin, "default");
        leaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                result("QUIT");
            }
        });

        setModal(true);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
                    hide();
                    return true;
                }
                return false;
            }
        });

        Table buttonTable = getButtonTable();
        buttonTable.pad(PADDING_BUTTON_TABLE);
        buttonTable.add(label_slider_music);
        buttonTable.add(slider_music);
        buttonTable.row();
        buttonTable.add(label_slider_sounds);
        buttonTable.add(slider_sounds);
        buttonTable.row();
        buttonTable.add(leaveButton).colspan(2);
    }

    @Override
    protected void result(Object object) {
        if (object.equals("QUIT")) {
            Dialog dialog = new Dialog("Quit", skin) {
                {
                    text("You are sure you want to quit?");
                    button("Yes", true);
                    button("No", false);
                }

                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        controller.backToMainMenu();
                    }
                }
            };
            dialog.show(getStage());
        }
    }

}
