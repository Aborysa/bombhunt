package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.bombhunt.game.model.ecs.systems.DIRECTION_ENUM;

public class KillableComponent extends Component {
    public int health = 100;
    public int damage_received = 0;
    public float timer_damage = 0.2f;
    public float ttl_timer = timer_damage;
    public boolean is_colored = false;
    public float color_persistence = 0.4f;
    public float ttl_color = color_persistence;
    public DIRECTION_ENUM last_hit = DIRECTION_ENUM.DOWN;
}