package com.bombhunt.game.services.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.JsonValue;
import com.bombhunt.game.model.Level;

public class Assets {

    private final static String ASSETS_FILE_NAME = "preload.json";
    private final static Assets assets = new Assets(ASSETS_FILE_NAME);
    private final AssetManager assetManager = new AssetManager();

    public static Assets getInstance() {
        return Assets.assets;
    }

    private Assets(String assets_file_name) {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        setUpCustomLoaders(resolver);
        JsonValue preload = loadJsonAssetsFile(assets_file_name);
        loadObjects(preload);
    }

    private void setUpCustomLoaders(FileHandleResolver resolver) {
        assetManager.setLoader(JsonValue.class, new JsonLoader(resolver));
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(resolver));
        assetManager.setLoader(Level.class, new LevelLoader(resolver));
    }

    private JsonValue loadJsonAssetsFile(String assets_file_name) {
        assetManager.load(assets_file_name, JsonValue.class);
        assetManager.finishLoadingAsset(assets_file_name);
        return assetManager.get(assets_file_name);
    }

    private void loadObjects(JsonValue preload) {
        for (int i = 0; i < preload.size; i++) {
            JsonValue data = preload.get(i);
            try {
                loadObject(data);
            } catch (ClassNotFoundException e) {
                System.err.println(e);
            }
        }
    }

    private void loadObject(JsonValue data) throws ClassNotFoundException {
        String fileName = data.getString("file");
        String className = data.getString("type");
        Class objectType = Class.forName(className);
        assetManager.load(fileName, objectType);
    }

    // Tells the asset loader to preload an asset, this should be done as the game starts
    public void preLoad(String name, Class<?> type) {
        assetManager.load(name, type);
    }

    public <T> T get(String name, Class<T> type) {
        return assetManager.get(name, type);
    }

    public <T> T getSync(String name, Class<T> type) {
        if (!assetManager.isLoaded(name, type)) {
            assetManager.finishLoadingAsset(name);
        }
        return assetManager.get(name, type);
    }

    public boolean update() {
        return assetManager.update();
    }

    public void dispose() {
        assetManager.clear();
        // assetManager.dispose();
    }
}