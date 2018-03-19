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
    private static TextureRegion offsetRegion(TextureRegion texregion){
        float subpx_w = 0.5f/texregion.getTexture().getWidth();
        float subpx_h = 0.5f/texregion.getTexture().getHeight();

        /*
        texregion.setRegion(
                texregion.getU() + subpx_w,
                texregion.getV() + subpx_h,
                texregion.getU2() - subpx_w,
                texregion.getV2() - subpx_h
        );
        */
        return texregion;
    }

    private static <T extends TextureRegion> Array<T> offsetRegions(Array<T> texregions){
        for(TextureRegion region : texregions) {
          offsetRegion(region);
        }
        return texregions;
    }

    public static Sprite createSprite(TextureAtlas texatlas, String name){
        return (Sprite)offsetRegion(texatlas.createSprite(name));
    }


    public static Array<Sprite> createSprites(TextureAtlas texatlas, String name){
        return (Array<Sprite>) offsetRegions(texatlas.createSprites(name));
    }

    // Creats animation from a region in a texture atlas
    public static Animation<Sprite> createAnimation(TextureAtlas texatlas, String name, float fps){
        Array<Sprite> sprites = texatlas.createSprites(name);
        offsetRegions(sprites);
        return new Animation<Sprite>(1f/fps, sprites);
    }


    // Create sprites from a texture region
    public static Array<Sprite> createSprites(TextureRegion region, int tile_size, int x, int y, int len){
        TextureRegion[][] subregions = region.split(tile_size, tile_size);
        int tiles_x = region.getRegionWidth()/tile_size;
        System.out.println(region.getRegionWidth());
        System.out.println(tiles_x);
        Array<Sprite> sprites = new Array<Sprite>(len);
        for(int i = 0; i < len; i++){
              TextureRegion subregion = subregions[y + (int)(i/tiles_x)][x + i%tiles_x];
              System.out.println(subregion.getRegionWidth());
              sprites.add(new Sprite(subregion));
        }
        /*if(true){
            throw new RuntimeException(String.valueOf(subregions.length));
        }*/
        //return sprites;
        return offsetRegions(sprites);
    }

    public static Animation<Sprite> createAnimation(Array<Sprite> sprites, float fps){
        return new Animation<Sprite>(1f/fps, sprites);
    }



}