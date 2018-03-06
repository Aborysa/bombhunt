package com.bombhunt.game.view;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

public class GameScreen extends InputAdapter implements IView{

    @Override
    public void update(float dtime){}

    @Override
    public void render(){}

    @Override
    public void dispose(){}

    @Override
    public InputProcessor getInputProcessor() {
        return this;
    }


}
