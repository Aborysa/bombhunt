package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.EventListener;

/**
 * Created by erlin on 27.03.2018.
 */

public class TimerComponent extends Component {
    public float timer;
    public EventListener listener;
}
