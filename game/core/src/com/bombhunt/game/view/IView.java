package com.bombhunt.game.view;

import com.badlogic.gdx.InputProcessor;

// Currently IView provides an input processor, might want to
// replace that with a getViewController which handles input
public interface IView {
    void update(float dtime);

    void render();

    void dispose();

    InputProcessor getInputProcessor();
}
