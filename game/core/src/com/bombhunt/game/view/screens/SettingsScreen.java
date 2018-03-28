package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bombhunt.game.BombHunt;

/**
 * Created by samuel on 27/03/18.
 */

public class SettingsScreen extends MovingBackgroundScreen {

    public SettingsScreen(BombHunt bombHunt) {
        super(bombHunt);
        Texture background = new Texture(Gdx.files.internal("dynamitesBackground.png"));
        setBackground(background);
        Table main_table = feedMainTable();
        setTable(main_table);
    }

    private Table feedMainTable() {
        Label title = new Label("Settings", skin, "title");
        Table table = new Table();
        table.add(title).row();
        addReturnButton(table);
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
        batch.draw(background, -background.getWidth()-offsetBackgroundX, -offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, -offsetBackgroundX, -background.getHeight()-offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, -background.getWidth()-offsetBackgroundX, -background.getHeight()-offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

}
