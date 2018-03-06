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
  }

  private static void offsetRegions(Array<? extends TextureRegion> texregions){
    for(TextureRegion region : texregions) {
      offsetRegion(region);
    }
  }

  public Animation<Sprite> createAnimation(TextureAtlas texregion, String name, float fps){
    Array<Sprite> sprites = texregion.createSprites(name);
    offsetRegions(sprites);
    return new Animation<Sprite>(1f/fps, sprites);
  }

}