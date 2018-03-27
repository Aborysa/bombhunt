package com.bombhunt.game.view;


import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;


// Currently IView provides an input processor, might want to
// replace that with a getViewController which handles input
public interface IView {
    public void update(float dtime);
    public void render();
    public void dispose();
    public InputProcessor getInputProcessor();

}
