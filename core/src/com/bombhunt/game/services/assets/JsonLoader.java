package com.bombhunt.game.services.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;


// Basic json loader for the asset manager
public class JsonLoader extends SynchronousAssetLoader<JsonValue, JsonLoader.JsonLoaderParameters> {

    private JsonReader reader = new JsonReader();

    public JsonLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public JsonValue load(AssetManager assetManager, String filename, FileHandle file, JsonLoader.JsonLoaderParameters params) {
        return reader.parse(file);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String filename, FileHandle file, JsonLoader.JsonLoaderParameters params) {
        return new Array<>(+0);
    }

    static public class JsonLoaderParameters extends AssetLoaderParameters<JsonValue> {
    }
}
