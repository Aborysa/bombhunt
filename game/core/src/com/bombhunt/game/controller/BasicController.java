package com.bombhunt.game.controller;

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

    protected BasicController(BombHunt bombHunt) {
        this.bombHunt = bombHunt;
    }

    public ChangeListener createChangeListener(BasicView current_view, Class new_view_class) {
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    Constructor<?> cons = new_view_class.getConstructor(BombHunt.class);
                    BasicView new_view = (BasicView) cons.newInstance(bombHunt);
                    changeView(current_view, (BasicView) new_view);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        };
        return listener;
    }

    protected void changeView(BasicView current_view, BasicView new_view) {
        bombHunt.setCurrentView(new_view);
        current_view.dispose();
    }

}
