package com.bombhunt.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.bombhunt.game.BombHunt;

public abstract class BasicView extends InputAdapter {

    protected BombHunt bombHunt;

    public BasicView(BombHunt bombHunt) {
        this.bombHunt = bombHunt;
    }

    public abstract void update(float dtime);

    public abstract void render();

    public abstract void dispose();

    public InputProcessor getInputProcessor() {
        return null;
    }

    public void changeView(BasicView current_view, BasicView new_view) {
        bombHunt.setCurrentView(new_view);
        current_view.dispose();
    }

    protected void clearBackground() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
