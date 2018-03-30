package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.controller.SettingsController;
import com.bombhunt.game.utils.Assets;

/**
 * Created by samuel on 27/03/18.
 * references:
 * https://stackoverflow.com/questions/23833251/
 * https://www.programcreek.com/java-api-examples/index.php?api=com.badlogic.gdx.scenes.scene2d.ui.Slider
 */

public class SettingsScreen extends MovingBackgroundScreen {

    private SettingsController controller;

    public SettingsScreen(BombHunt bombHunt) {
        super(bombHunt);
        controller = SettingsController.getInstance(bombHunt);
        String theme_song = "unfinishedBusiness.mp3";
        controller.setNewThemeSong(theme_song);
        Texture background = Assets.getInstance().get("dynamitesBackground.png", Texture.class);
        setBackground(background);
        Table main_table = feedMainTable();
        setTable(main_table);
    }

    private Table feedMainTable() {
        Table table = new Table();
        addTitle(table);
        addSliders(table);
        addButtons(table);
        table.setFillParent(true);
        return table;
    }

    @Override
    void updateMovingBackgroundPosition() {
        offsetBackgroundX = (offsetBackgroundX - OFFSET_BACKGROUND_STEP_X) % background.getWidth();
        offsetBackgroundY = (offsetBackgroundY - OFFSET_BACKGROUND_STEP_Y) % background.getHeight();
    }

    @Override
    void drawMovingBackground(SpriteBatch batch) {
        batch.draw(background, -offsetBackgroundX, -offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, -background.getWidth() - offsetBackgroundX, -offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, -offsetBackgroundX, -background.getHeight() - offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, -background.getWidth() - offsetBackgroundX, -background.getHeight() - offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void addTitle(Table table) {
        Label title = new Label("Settings", skin, "title");
        table.add(title).colspan(4).padBottom(10).row();
    }

    private void addSliders(Table table) {
        addSliderMusic(table);
        addSliderSoundFX(table);
    }

    private void addSliderMusic(Table table) {
        Label label_music = new Label("Music", skin, "default");
        Slider slider_music = createMusicSlider();
        table.add(label_music).right().expandX();
        table.add(slider_music).left().padLeft(25).expandX().padBottom(10).row();
    }

    private void addSliderSoundFX(Table table) {
        Label label_soundFX = new Label("Sound FX", skin, "default");
        Slider slider_soundFX = createSoundFXSlider();
        table.add(label_soundFX).right().expandX();
        table.add(slider_soundFX).left().padLeft(25).expandX().padBottom(10).row();
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

    private Slider createSoundFXSlider() {
        Slider slider_soundFX = new Slider(0, 100, 1, false, skin);
        float current_volume_sound = controller.getVolumeSound();
        slider_soundFX.setValue(current_volume_sound*100);
        slider_soundFX.getStyle().knob.setMinHeight(100);
        Runnable runnable_sound = new Runnable() {
            @Override
            public void run() {
                controller.setVolumeSoundFX(slider_soundFX.getValue()/100);
            }
        };
        ChangeListener listener_volume_sound = controller.createChangeListener(runnable_sound);
        slider_soundFX.addListener(listener_volume_sound);
        return slider_soundFX;
    }

    private void addButtons(Table table) {
        ChangeListener listener = controller.createViewTransitionWithSoundListener(this, MainMenuScreen.class);
        addReturnButton(table, listener,4);
    }

}
