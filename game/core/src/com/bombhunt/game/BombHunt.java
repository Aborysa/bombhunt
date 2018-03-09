package com.bombhunt.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bombhunt.game.view.GameScreen;
import com.bombhunt.game.view.IView;

public class BombHunt extends ApplicationAdapter {

  IView currentView;

  public void setCurrentView(IView view){
    // May want to despose old view
    Gdx.input.setInputProcessor(view.getInputProcessor());
    currentView = view;
  }

  @Override
  public void create () {
    setCurrentView(new GameScreen());

    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
  }

  @Override
  public void render () {
    float dtime = Gdx.graphics.getDeltaTime();

    currentView.update(dtime);

    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    currentView.render();

  }
  
  @Override
  public void dispose () {
  }
}
