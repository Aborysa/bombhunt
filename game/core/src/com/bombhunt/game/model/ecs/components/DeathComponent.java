package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;

public class DeathComponent extends Component {
    public float timer = 2f;
    public float ttl_timer = timer;
    public float alpha = 1;
    public float blinking_time = 0.3f;
}