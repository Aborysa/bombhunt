package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.controller.SettingsController;

/**
 * Created by samuel on 27/03/18.
 * reference: https://stackoverflow.com/questions/23833251/
 */

public class SettingsScreen extends MovingBackgroundScreen {

    private SettingsController controller;

    public SettingsScreen(BombHunt bombHunt) {
        super(bombHunt);
        controller = SettingsController.getInstance(bombHunt);

        String theme_song = "unfinishedBusiness.mp3";
        controller.setNewThemeSong(theme_song);

        Texture background = new Texture(Gdx.files.internal("dynamitesBackground.png"));
        setBackground(background);
        Table main_table = feedMainTable();
        setTable(main_table);
    }

    private Table feedMainTable() {
        Label title = new Label("Settings", skin, "title");
        Table table = new Table();
        table.add(title).colspan(4).padBottom(10).row();
        addSliders(table);
        ChangeListener listener = controller.createChangeListener(this, MainMenuScreen.class);
        addReturnButton(table, listener,4);
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

    private void addSliders(Table table) {
        Label label_volume = new Label("Volume", skin, "default");
        Slider slider_volume = new Slider(0, 100, 1, false, skin);
        slider_volume.setValue(50);
        slider_volume.getStyle().knob.setMinHeight(100);
        Label label_soundFX = new Label("Sound FX", skin, "default");
        Slider slider_soundFX = new Slider(0, 100, 1, false, skin);
        slider_soundFX.setValue(50);
        table.add(label_volume).right().expandX();
        table.add(slider_volume).left().padLeft(25).expandX().padBottom(10).row();
        table.add(label_soundFX).right().expandX();
        table.add(slider_soundFX).left().padLeft(25).expandX().padBottom(10).row();
    }

}
