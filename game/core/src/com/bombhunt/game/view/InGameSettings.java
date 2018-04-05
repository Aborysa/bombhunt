package com.bombhunt.game.view;

import com.badlogic.gdx.audio.Sound;
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
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.audio.AudioPlayer;

import javax.xml.soap.Text;

/**
 * Created by samuel on 05/04/18.
 * references:
 * https://www.youtube.com/watch?v=fxkuHa9FmGw
 * https://stackoverflow.com/questions/29771114
 * http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=22867
 */

public class InGameSettings extends Dialog {

    private static final String SETTINGS_DIALOG_TITLE = "Settings";
    private static final String QUIT_DIALOG_TITLE = "Quit";
    private final int PADDING_TABLE = 50;

    private InGameSettingsController controller;
    private Skin skin;

    public InGameSettings(Skin skin, BombHunt bombHunt) {
        super(SETTINGS_DIALOG_TITLE, skin);
        this.controller = InGameSettingsController.getInstance(bombHunt);
        this.skin = skin;
        feedMainTable();
        addExternalClickLeaveOption();
    }

    private void feedMainTable() {
        Table buttonTable = getButtonTable();
        buttonTable.pad(PADDING_TABLE);
        addMusicSlider(buttonTable);
        addSoundSlider(buttonTable);
        addQuitButton(buttonTable);
    }

    private void addMusicSlider(Table table) {
        Label label_slider_music = new Label("Music", skin, "default");
        Slider slider_music = BasicSettingsView.createMusicSlider(controller, skin);
        table.add(label_slider_music);
        table.add(slider_music);
        table.row();
    }

    private void addSoundSlider(Table table) {
        Label label_slider_sounds = new Label("Sound FX", skin, "default");
        Slider slider_sounds = BasicSettingsView.createSoundFXSlider(controller, skin);
        table.add(label_slider_sounds);
        table.add(slider_sounds);
        table.row();
    }

    private void addQuitButton(Table table) {
        TextButton leaveButton = new TextButton("Leave Game", skin, "default");
        Assets asset_manager = Assets.getInstance();
        Sound sound = asset_manager.get("accessDenied.wav", Sound.class);
        leaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                result("QUIT");
                controller.playSound(sound, 1);
            }
        });
        table.add(leaveButton).colspan(2);
    }

    private void addExternalClickLeaveOption() {
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
            Dialog dialog = createQuitConfirmationDialog();
            hide();
            dialog.show(getStage());
        }
    }

    private Dialog createQuitConfirmationDialog() {
        return new Dialog(QUIT_DIALOG_TITLE, skin) {
            {
                Table contentTable = getContentTable();
                contentTable.padTop(PADDING_TABLE);
                Table buttonTable = getButtonTable();
                buttonTable.pad(PADDING_TABLE);
                text("Do you really want to quit?");
                TextButton buttonYes = createTextButtonWithSound("Yes", true);
                buttonTable.add(buttonYes);
                TextButton buttonNo = createTextButtonWithSound("No", false);
                buttonTable.add(buttonNo);
            }

            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    controller.backToMainMenu();
                } else {
                    hide();
                    Dialog dialog = new InGameSettings(skin, controller.getBombHunt());
                    dialog.show(getStage());
                }
            }

            private TextButton createTextButtonWithSound(String text, Object result) {
                TextButton button = new TextButton(text, skin, "default");
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        result(result);
                        Assets asset_manager = Assets.getInstance();
                        Sound sound = asset_manager.get("buttonClickInGame.wav", Sound.class);
                        AudioPlayer audioPlayer = AudioPlayer.getInstance();
                        audioPlayer.playSound(sound);
                    }
                });
                return button;
            }
        };
    }
}
