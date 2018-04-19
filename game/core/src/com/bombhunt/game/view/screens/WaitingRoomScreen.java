package com.bombhunt.game.view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.controller.WaitingRoomController;
import com.bombhunt.game.view.BasicView;

/**
 * Created by erlin on 15.04.2018.
 */



public class WaitingRoomScreen extends BasicView {

    private boolean created = false;
    public boolean leftRoom = false;
    public boolean joinedRoom = false;
    private WaitingRoomController controller;
    public WaitingRoomScreen(BombHunt bombHunt) {
        controller = new WaitingRoomController(bombHunt, this);
        controller.setSender(bombHunt.getPlayServices());

    }

    @Override
    public void update(float dt) {


    }

    @Override
    public void render() {
        clearBackground();
        if(!created){
            createRoom();
            created = true;
        }
        if(leftRoom){
            controller.backToMainMenu();
        }
        if(joinedRoom){
            controller.enterGameScreen();
        }
    }

    private void createRoom(){

        controller.getBombHunt().getPlayServices().setRoomListener(controller);
        controller.getBombHunt().getPlayServices().startMatchMaking();
    }
}
