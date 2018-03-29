package com.bombhunt.game.controller;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.BasicView;

/**
 * Created by samuel on 29/03/18.
 */

public class MainMenuController extends BasicController {

    private static MainMenuController instance;

    private MainMenuController(BombHunt bombHunt) {
        super(bombHunt);
    }

    public static MainMenuController getInstance(BombHunt bombHunt) {
        if (instance == null) {
            instance = new MainMenuController(bombHunt);
        }
        return instance;
    }

    public ChangeListener createQuitListener(BasicView current_view) {
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                current_view.dispose();
                System.exit(-1);
            }
        };
        return listener;
    }



}
