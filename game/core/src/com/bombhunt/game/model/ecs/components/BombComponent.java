package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;

/**
 * Created by erlin on 27.03.2018.
 */

public class BombComponent extends Component {
    public int range = 5;
    public int damage = 10;
    public float timer = 2f;
}
