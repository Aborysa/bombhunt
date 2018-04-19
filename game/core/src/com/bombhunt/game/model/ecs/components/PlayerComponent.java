package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;

public class PlayerComponent extends Component {
    public float movement_speed = 2f;
    public float cooldown_bomb = 0.5f;
    public float ttl_cooldown_bomb = cooldown_bomb;
    public Boolean isCooledDownBomb = false;
    public int max_health = 100;
    public int bomb_damage = 10;
    public int bomb_range = 1;
}
