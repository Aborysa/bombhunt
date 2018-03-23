package com.bombhunt.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bombhunt.game.networking.PlayServices;
import com.bombhunt.game.utils.Assets;
import com.bombhunt.game.view.GameScreen;
import com.bombhunt.game.view.IView;

public class BombHunt extends ApplicationAdapter {

  IView currentView;

  boolean assetsLoaded = false;

  PlayServices playServices;
  public BombHunt(PlayServices playServices){
    this.playServices = playServices;
    playServices.signIn();
  }

  public void setCurrentView(IView view){
    // May want to despose old view
    Gdx.input.setInputProcessor(view.getInputProcessor());
    currentView = view;
  }

  @Override
  public void create () {

    Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
    Gdx.gl.glDepthMask(true);
    Gdx.gl.glDisable(GL20.GL_BLEND);
    Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);

  }

  @Override
  public void render () {
    // Temp setup for loading assets
    if(!assetsLoaded){
      assetsLoaded = Assets.getInstance().update();
      if(assetsLoaded) {
        // Once loaded set the current view to the game screen
        setCurrentView(new GameScreen());
      }
      return;
    }


    float dtime = Gdx.graphics.getDeltaTime();

    currentView.update(dtime);

    Gdx.gl.glClearColor(0.2f, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    currentView.render();

  }
  
  @Override
  public void dispose () {
  }
}
