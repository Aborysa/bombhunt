package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.badlogic.gdx.math.Vector3;
import com.bombhunt.game.model.ecs.systems.DIRECTION_ENUM;

@DelayedComponentRemoval
public class PlayerComponent extends Component {
    public float movement_speed = 1.2f;
    public float bomb_cooldown = 1f;
    public Boolean isCooledDownBomb = false;
    public float ttl_bomb_cooldown = bomb_cooldown;
    public int max_health = 100;
    public int malus = 0;
    public int bomb_damage = 10;
    public int bomb_range = 1;
    public DIRECTION_ENUM direction = DIRECTION_ENUM.DOWN;
    public DIRECTION_ENUM prev_direction = DIRECTION_ENUM.DOWN;

    public int index = 0;

    public boolean is_dead = false;
    public DIRECTION_ENUM last_hit = DIRECTION_ENUM.DOWN;
}
