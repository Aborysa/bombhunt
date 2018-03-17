package com.bombhunt.game.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Erling on 17/03/2018.
 *
 */

public class Renderer extends Component {
    // looks of entity, use this to draw etcetc
    public Sprite sprite;

    public Renderer(TextureRegion textureRegion){
        sprite = new Sprite(textureRegion);
    }

}
