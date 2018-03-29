package com.bombhunt.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.bombhunt.game.networking.PlayServices;
import com.bombhunt.game.service.AudioPlayer;
import com.bombhunt.game.utils.Assets;
import com.bombhunt.game.view.screens.MainMenuScreen;
import com.bombhunt.game.view.BasicView;

public class BombHunt extends ApplicationAdapter {
    private BasicView currentView;
    private boolean assetsLoaded = false;
    private boolean viewLoaded = false;
    PlayServices playServices;
    public AudioPlayer audioPlayer;

    public BombHunt(PlayServices playServices) {
        this.playServices = playServices;
        playServices.signIn();
        audioPlayer = new AudioPlayer();
    }

    public void setCurrentView(BasicView view) {
        // May want to dispose old view
        Gdx.input.setInputProcessor(view.getInputProcessor());
        currentView = view;
    }

    @Override
    public void create() {
        Box2D.init();
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
    }

    @Override
    public void render() {
        loadMainMenuScreen();
        float dtime = Gdx.graphics.getDeltaTime();
        currentView.update(dtime);
        clearScreen();
        currentView.render();
    }

    @Override
    public void dispose() {
        currentView.dispose();
        audioPlayer.dispose();
        //Assets.getInstance().dispose();
    }

    private void loadMainMenuScreen() {
        while (!viewLoaded) {
            if (isAssetsLoaded()) {
                setCurrentView(new MainMenuScreen(this));
                viewLoaded = true;
            }
        }
    }

    private boolean isAssetsLoaded() {
        // Temp setup for loading assets
        if (assetsLoaded) {
            return true;
        } else {
            assetsLoaded = Assets.getInstance().update();
            return assetsLoaded;
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0.2f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }
}
