package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.bombhunt.game.model.ecs.factories.ItemFactory;


public class ItemComponent extends Component {
    public ItemFactory.ItemType type;
    public float timeout = 10f;
    public float flickerTime = 3f;

}
