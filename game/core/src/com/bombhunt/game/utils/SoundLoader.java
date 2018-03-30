package com.bombhunt.game.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * Created by samuel on 30/03/18.
 */

public class SoundLoader  extends SynchronousAssetLoader<Sound, SoundLoader.SoundLoaderParameters> {


    public SoundLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Sound load(AssetManager assetManager, String fileName, FileHandle file, SoundLoader.SoundLoaderParameters parameter) {
        return assetManager.get(fileName, Sound.class);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, SoundLoader.SoundLoaderParameters parameter) {
        Array<AssetDescriptor> dependencies = new Array<>();
        dependencies.add(new AssetDescriptor<>(file, Sound.class));
        return dependencies;
    }

    class SoundLoaderParameters extends AssetLoaderParameters<Sound> {

    }
}
