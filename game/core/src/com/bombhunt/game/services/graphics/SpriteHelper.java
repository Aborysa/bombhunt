package com.bombhunt.game.services.graphics;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Array;

// Helper for creating individual and animated sprites
public class SpriteHelper {

    //Offsets the TextureRegion's uv-coords by 0.5f
    //fixes the opengl subpixel alignment
    private static TextureRegion offsetRegion(TextureRegion textureRegion) {
        float subpx_w = 0.5f / textureRegion.getTexture().getWidth();
        float subpx_h = 0.5f / textureRegion.getTexture().getHeight();

    /*
    textureRegion.setRegion(
            textureRegion.getU() + subpx_w,
            textureRegion.getV() + subpx_h,
            textureRegion.getU2() - subpx_w,
            textureRegion.getV2() - subpx_h
    );
    */
        return textureRegion;
    }

    private static <T extends TextureRegion> Array<T> offsetRegions(Array<T> textureRegions) {
        for (TextureRegion region : textureRegions) {
            offsetRegion(region);
        }
        return textureRegions;
    }

    public static Sprite createSprite(TextureAtlas textureAtlas, String name) {
        return (Sprite) offsetRegion(textureAtlas.createSprite(name));
    }


    public static Array<Sprite> createSprites(TextureAtlas textureAtlas, String name) {
        return (Array<Sprite>) offsetRegions(textureAtlas.createSprites(name));
    }

    // Creats animation from a region in a texture atlas
    public static Animation<Sprite> createAnimation(TextureAtlas textureAtlas, String name, float fps) {
        Array<Sprite> sprites = textureAtlas.createSprites(name);
        offsetRegions(sprites);
        return new Animation<Sprite>(1f / fps, sprites);
    }


    // Create sprites from a texture region
    public static Array<Sprite> createSprites(TextureRegion region, int tileSize, int x, int y, int len) {
        TextureRegion[][] subregions = region.split(tileSize, tileSize);
        int tiles_x = region.getRegionWidth() / tileSize;
        //System.out.println(region.getRegionWidth());
        //System.out.println(tiles_x);
        Array<Sprite> sprites = new Array<Sprite>(len);
        for (int i = 0; i < len; i++) {
            TextureRegion subregion = subregions[y + (int) (i / tiles_x)][x + i % tiles_x];
            //System.out.println(subregion.getRegionWidth());
            sprites.add(new Sprite(subregion));
        }
    /*if(true){
        throw new RuntimeException(String.valueOf(subregions.length));
    }*/
        //return sprites;
        return offsetRegions(sprites);
    }

    public static Animation<Sprite> createAnimation(Array<Sprite> sprites, float fps) {
        return new Animation<Sprite>(1f / fps, sprites);
    }

    public static Animation<Decal> createDecalAnimation(Array<Sprite> sprites, float fps) {
        Array<Decal> decals = new Array<Decal>(sprites.size);
        for (Sprite sprite : sprites) {
            decals.add(Decal.newDecal(sprite, true));
        }
        return new Animation<Decal>(1f / fps, decals);
    }
}