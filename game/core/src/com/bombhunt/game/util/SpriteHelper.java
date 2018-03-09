package com.bombhunt.game.util;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.Collection;

// Helper for creating individual and animated sprites
public class SpriteHelper {
  
  //Offsets the TextureRegion's uv-coords by 0.5f
  //fixes the opengl subpixel alignment
  private static void offsetRegion(TextureRegion texregion){
    texregion.setRegion(
            texregion.getU() + 0.5f,
            texregion.getV() + 0.5f,
            texregion.getU2() - 0.5f,
            texregion.getV2() - 0.5f
    );
    return texregion;
  }

  private static Array<? extends TextureRegion> offsetRegions(Array<? extends TextureRegion> texregions){
    for(TextureRegion region : texregions) {
      offsetRegion(region);
    }
    return texregions;
  }

  public static Sprite createSprite(TextureAtlast texatlas, String name){
    return texatlas.createSprite(offsetRegion(texregion), name);
  }

  public static Array<Sprite> createSprites(TextureAtlas texatlas, Spring name){
    return offsetRegions(texatlas.createSprites(texatlas, name));
  }

  public static Animation<Sprite> createAnimation(TextureAtlas texatlas, String name, float fps){
    Array<Sprite> sprites = texatlas.createSprites(name);
    offsetRegions(sprites);
    return new Animation<Sprite>(1f/fps, sprites);
  }

}