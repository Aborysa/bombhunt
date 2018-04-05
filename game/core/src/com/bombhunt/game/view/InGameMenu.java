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
import com.bombhunt.game.controller.GameController;

/**
 * Created by samuel on 05/04/18.
 * references:
 * https://www.youtube.com/watch?v=fxkuHa9FmGw
 * https://stackoverflow.com/questions/29771114
 * http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=22867
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
        buttonTable.row();
        TextButton leaveButton = new TextButton("Leave Game", skin, "default");
        leaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                result("QUIT");
            }
        });
        buttonTable.add(leaveButton).colspan(2);
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

    private Slider createMusicSlider() {
        Slider slider_music = new Slider(0, 100, 1, false, skin);
        float current_volume_music = controller.getVolumeMusic();
        slider_music.setValue(current_volume_music * 100);
        slider_music.getStyle().knob.setMinHeight(100);
        Runnable runnable_music = new Runnable() {
            @Override
            public void run() {
                controller.setVolumeMusic(slider_music.getValue() / 100);
            }
        };
        ChangeListener listener_volume_music = controller.createChangeListener(runnable_music);
        slider_music.addListener(listener_volume_music);
        return slider_music;
    }

}
