package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;

public class DestroyableComponent extends Component {
    public int health = 1;
    public float timer_destruction = 0.3f;
}