package com.bombhunt.game.utils.level;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

public class LevelLoader extends SynchronousAssetLoader<Level, LevelLoader.LevelLoaderParameters> {

  public LevelLoader(FileHandleResolver resolver){
    super(resolver);
  }

  @Override
  public Level load(AssetManager assetManager, String filename, FileHandle file, LevelLoader.LevelLoaderParameters params){
    TiledMap map = assetManager.get(filename, TiledMap.class);
    Level level = new Level(map);

    return level;
  }

  @Override
  public  Array<AssetDescriptor> getDependencies(final String filename, FileHandle file, LevelLoader.LevelLoaderParameters params){
    Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
    dependencies.add(new AssetDescriptor<TiledMap>(file, TiledMap.class));
    
    return dependencies;
  }


  public class LevelLoaderParameters extends AssetLoaderParameters<Level>{

  }
}