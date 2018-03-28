package com.bombhunt.game.view.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.BasicView;

public class MainMenuScreen extends MovingBackgroundScreen {

    public MainMenuScreen(BombHunt bombHunt) {
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

    private TextButton createButton(String text, ChangeListener listener) {
        TextButton button = new TextButton(text, skin, "default");
        button.setTransform(true);
        button.addListener(listener);
        return button;
    }

    private ChangeListener createChangeListener(BasicView current_view, BasicView new_view) {
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeView(current_view, new_view);
            }
        };
        return listener;
    }

    private ChangeListener createQuitListener(BasicView current_view, BombHunt bombHunt) {
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                current_view.dispose();
                System.exit(-1);
            }
        };
        return listener;
    }

    private Table feedMainTable() {
        Label title = new Label("Bomb Hunt", skin, "title");
        TextButton btnPlay = createButton("Play",
                createChangeListener(this, new GameScreen(bombHunt)));
        TextButton btnSettings = createButton("Settings",
                createChangeListener(this, new SettingsScreen(bombHunt)));
        TextButton btnCredits = createButton("Credits",
                createChangeListener(this, new CreditsScreen(bombHunt)));
        TextButton btnQuit = createButton("Quit",
                createQuitListener(this, bombHunt));
        Table table = new Table();
        table.add(title).colspan(3).row();
        table.add(btnPlay).expandX().center().padBottom(10);
        table.add(btnSettings).expandX().center().padBottom(10).row();
        table.add(btnCredits).expandX().center().padBottom(10);
        table.add(btnQuit).expandX();
        table.setFillParent(true);
        return table;
    }
}
