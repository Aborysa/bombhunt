package com.bombhunt.game.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.controller.CreditsController;
import com.bombhunt.game.utils.Assets;

/**
 * Created by samuel on 27/03/18.
 */

public class CreditsScreen extends MovingBackgroundScreen {

    private CreditsController controller;

    public CreditsScreen(BombHunt bombHunt) {
        super(bombHunt);
        controller = CreditsController.getInstance(bombHunt);
        Texture background = Assets.getInstance().get("angryBombsBackground.png", Texture.class);
        setBackground(background);
        Table main_table = feedMainTable();
        setTable(main_table);
    }

    private Table feedMainTable() {
        Table table = new Table();
        table.setFillParent(true);
        ScrollPane scroller = feedScrollPane();
        table.add(scroller).top().fill().expand().row();
        return table;
    }

    private ScrollPane feedScrollPane() {
        Table scroll_table = feedScrollTable();
        ScrollPane scroller = new ScrollPane(scroll_table);
        return scroller;
    }

    private Table feedScrollTable() {
        Table scroll_table = new Table();
        addTitle(scroll_table);
        addDevelopersToTable(scroll_table);
        addCourseStaff(scroll_table);
        addArtists(scroll_table);
        ChangeListener listener = controller.createViewTransitionListener(this, MainMenuScreen.class);
        addReturnButton(scroll_table, listener, 1);
        return scroll_table;
    }

    private void addTitle(Table table) {
        Label title = new Label("Credits", skin, "title");
        table.add(title).top().row();
    }

    private void addDevelopersToTable(Table table) {
        addFromFile(table, "Developers", "developers.txt");
    }

    private void addCourseStaff(Table table) {
        addFromFile(table, "TDT4240 - Course Staff", "tdt4240Staff.txt");
    }

    private void addArtists(Table table) {
        addFromFile(table, "Artists", "artists.txt");
    }

    private void addFromFile(Table table, String section_title, String file_name) {
        table.add(new Label(section_title, skin, "bold")).row();
        FileHandle file = Gdx.files.internal(file_name);
        String lines[] = file.readString("UTF-8").split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            table.add(new Label(lines[i], skin, "default")).row();
        }
        table.add(new Label("", skin)).row();
    }

    @Override
    void updateMovingBackgroundPosition() {
        offsetBackgroundX = (offsetBackgroundX + OFFSET_BACKGROUND_STEP_X) % background.getWidth();
        offsetBackgroundY = (offsetBackgroundY + OFFSET_BACKGROUND_STEP_Y) % background.getHeight();
    }

    @Override
    void drawMovingBackground(SpriteBatch batch) {
        batch.draw(background, -offsetBackgroundX, -offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, background.getWidth()-offsetBackgroundX, -offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, -offsetBackgroundX, background.getHeight()-offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(background, background.getWidth()-offsetBackgroundX, background.getHeight()-offsetBackgroundY,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}
