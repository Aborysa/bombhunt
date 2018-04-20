package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;

public class PlayerComponent extends Component {
    public float movement_speed = 1.2f;
    public float cooldown_bomb = 1f;
    public Boolean isCooledDownBomb = false;
    public float ttl_cooldown_bomb = cooldown_bomb;
    public int max_health = 100;
    public int health = max_health;
    public int bomb_damage = 10;
    public int bomb_range = 1;
}
