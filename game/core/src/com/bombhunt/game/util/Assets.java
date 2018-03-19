package com.bombhunt.game.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import io.reactivex.Observable;
import io.reactivex.internal.operators.observable.ObservableAll;


public class Assets{
  

    private static Assets assets = new Assets();
    private AssetManager assetManager = new AssetManager();

    public static Assets getInstance(){
    return Assets.assets;
    }


    private Assets(){
        FileHandleResolver resolver = new InternalFileHandleResolver();
        // Setup custom loaders here
        //assetManager.setLoader(Type.class, new Loader(resolver), param);
        assetManager.setLoader(TiledMap.class, ".tmx", new TmxMapLoader(resolver));

    }

    // Tells the asset loader to preload an asset, this should be done as the game starts
    public void preLoad(String name, Class<?> type){
        assetManager.load(name, type);
    }

    public <T> T get(String name, Class<T> type){
        return assetManager.get(name, type);
    }

    public <T> T getSync(String name, Class<T> type){
        if(!assetManager.isLoaded(name, type)){
            assetManager.finishLoadingAsset(name);
        }
        return assetManager.get(name, type);
    }

    // May want to add listener for when the game is finished loading
    public boolean update(){
        return assetManager.update();
    }

    public void dispose(){
        assetManager.dispose();
    }

}