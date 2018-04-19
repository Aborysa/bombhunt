package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.bombhunt.game.model.ecs.factories.ItemType;


public class ItemComponent extends Component {
    public ItemType type;
    public float timeout = 10f;
    public float flickerTime = 3f;
}
