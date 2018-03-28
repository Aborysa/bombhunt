package com.bombhunt.game.view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bombhunt.game.BombHunt;

/**
 * Created by samuel on 27/03/18.
 */

public class SettingsScreen extends MovingBackgroundScreen {

    public SettingsScreen(BombHunt bombHunt) {
        super(bombHunt);
        Table main_table = feedMainTable();
        setTable(main_table);
    }

    @Override
    public void update(float dtime) {

    }

    @Override
    public void render() {
        clearBackground();
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {

    }

    private Table feedMainTable() {
        Label title = new Label("Settings", skin, "title");
        Table table = new Table();
        table.add(title);
        table.setFillParent(true);
        return table;
    }

}
