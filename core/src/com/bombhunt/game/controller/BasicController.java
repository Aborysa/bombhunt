package com.bombhunt.game.controller;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.view.BasicView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by samuel on 29/03/18.
 */

public class BasicController {

    protected BombHunt bombHunt;

    BasicController(BombHunt bombHunt) {
        this.bombHunt = bombHunt;
    }

    public ChangeListener createViewTransitionListener(Class<? extends BasicView> new_view_class) {
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    Constructor<?> cons = new_view_class.getConstructor(BombHunt.class);
                    BasicView new_view = (BasicView) cons.newInstance(bombHunt);
                    changeView((BasicView) new_view);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        };
        return listener;
    }

    public ChangeListener createViewTransitionWithSoundListener(Class<? extends BasicView> new_view_class) {
        ChangeListener listener = createViewTransitionListener(new_view_class);
        ChangeListener bonified_listener = bonifySoundListener(listener);
        return bonified_listener;
    }

    protected ChangeListener bonifySoundListener(ChangeListener listener) {
        ChangeListener bonified_listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bombHunt.audioPlayer.playButtonSound();
                listener.changed(event, actor);
            }
        };
        return bonified_listener;
    }

    protected void changeView(BasicView new_view) {
        BasicView current_view = bombHunt.getCurrentView();
        bombHunt.setCurrentView(new_view);
        current_view.dispose();
    }

    public void setNewThemeSong(String theme_song) {
        bombHunt.audioPlayer.setNewThemeSong(theme_song);
    }

    public BombHunt getBombHunt() {
        return bombHunt;
    }

    public void playSound(Sound sound, float factor) {
        bombHunt.audioPlayer.playSoundWithFactor(sound, factor);
    }
}
