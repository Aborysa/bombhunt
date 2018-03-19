package com.bombhunt.game.util;

import com.badlogic.gdx.assets.AssetManager;


public class Assets{
  

    private static Assets assets = new Assets();
    private AssetManager assetManager = new AssetManager();


    public static Assets getInstance(){
    return Assets.assets;
    }



}