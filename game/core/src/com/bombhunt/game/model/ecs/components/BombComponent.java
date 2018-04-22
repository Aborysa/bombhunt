package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;

/**
 * Created by erlin on 27.03.2018.
 */

public class BombComponent extends Component {
    public float timer = 3f;
    public float ttl_timer = timer;
    public int damage = 10;
    public int range = 1;
}
