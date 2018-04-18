package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by erlin on 27.03.2018.
 */

public class ExplosionComponent extends Component {
    public float duration = 0.3f;
    public int range = 1;
    public boolean is_decaded = false;
    public Vector3 direction = null;
    public float time_decade = 0.2f; //must be lower than duration
    public float ttl_decade = time_decade;
    public int damage = 10;
}
