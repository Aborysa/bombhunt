package com.bombhunt.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.bombhunt.game.services.assets.Assets;
import com.bombhunt.game.services.audio.AudioPlayer;
import com.bombhunt.game.services.networking.IPlayServices;
import com.bombhunt.game.view.BasicView;
import com.bombhunt.game.view.screens.MainMenuScreen;

public class BombHunt extends ApplicationAdapter {
    private BasicView currentView;
    private boolean assetsLoaded = false;
    private boolean viewLoaded = false;
    IPlayServices playServices;
    public AudioPlayer audioPlayer;

    public BombHunt(IPlayServices playServices) {
        this.playServices = playServices;
        playServices.signIn();
        audioPlayer = AudioPlayer.getInstance();
    }

    public BasicView getCurrentView() {
        return currentView;
    }

    public void setCurrentView(BasicView view) {
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
        float dt = Gdx.graphics.getDeltaTime();
        currentView.update(dt);
        clearScreen();
        currentView.render();
    }

    @Override
    public void dispose() {
        currentView.dispose();
        audioPlayer.dispose();
        //TODO: Assets.getInstance().dispose();
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
        if (assetsLoaded) {
            return true;
        } else {
            assetsLoaded = Assets.getInstance().update();
            return assetsLoaded;
        }
    }

    private void clearScreen() {
        // reference: https://github.com/libgdx/libgdx/wiki/Clearing-the-screen
        Gdx.gl.glClearColor(0.2f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }
}
