package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;

public class KillableComponent extends Component {
    public int health = 100;
    public float timer_damage = 0.1f;
    public float ttl_timer = timer_damage;
    public int damage_received = 0;
}