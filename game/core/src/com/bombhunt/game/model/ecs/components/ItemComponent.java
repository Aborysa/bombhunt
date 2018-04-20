package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.bombhunt.game.model.ecs.factories.ITEM_TYPE_ENUM;


public class ItemComponent extends Component {
    public ITEM_TYPE_ENUM type;
    public float ttl_timer = 10f;
    public float blinkingTime = 3f;
}
