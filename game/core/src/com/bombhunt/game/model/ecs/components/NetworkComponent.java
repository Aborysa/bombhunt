package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.bombhunt.game.model.Grid;


public class NetworkComponent extends Component {
    public String owner = null;
    public int turn = 0;
    public int netId = -1;
}
