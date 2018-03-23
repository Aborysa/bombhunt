package com.bombhunt.game.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.JsonValue;
import com.bombhunt.game.utils.json.JsonLoader;
import com.bombhunt.game.utils.level.Level;
import com.bombhunt.game.utils.level.LevelLoader;

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
    assetManager.setLoader(TiledMap.class, new TmxMapLoader(resolver));
    assetManager.setLoader(JsonValue.class, new JsonLoader(resolver));
    assetManager.setLoader(Level.class, new LevelLoader(resolver));

    assetManager.load("preload.json", JsonValue.class);
    assetManager.finishLoadingAsset("preload.json");

    JsonValue preload = assetManager.get("preload.json");
    for(int i = 0; i < preload.size; i++){
      JsonValue data = preload.get(i);
      try{
        assetManager.load( data.getString("file"), Class.forName(data.getString("type")));
      }catch(ClassNotFoundException e){
        System.err.println(e);
      }
    }

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