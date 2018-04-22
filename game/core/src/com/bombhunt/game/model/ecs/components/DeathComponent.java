package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;

public class DeathComponent extends Component {
    public float timer = 2f;
    public float ttl_timer = timer;
    public float alpha = 1;
    public int update_opacity_frequency = 10;
    public float opacity_step = 0.01f;
    public float blinking_time = 0.3f;
    public boolean is_skull_displayed = false;
}