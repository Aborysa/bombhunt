package com.bombhunt.game.controller;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.services.networking.IPlayServices;
import com.bombhunt.game.view.BasicView;
import com.bombhunt.game.view.screens.MainMenuScreen;
import com.bombhunt.game.view.screens.WaitingRoomScreen;

/**
 * Created by samuel on 29/03/18.
 */

public class MainMenuController extends BasicController {

    public MainMenuController(BombHunt bombHunt) {
        super(bombHunt);
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

    public ChangeListener createQuitWithSoundListener(BasicView current_view) {
        ChangeListener listener = createQuitListener(current_view);
        ChangeListener bonified_listener = bonifySoundListener(listener);
        return bonified_listener;
    }
}
