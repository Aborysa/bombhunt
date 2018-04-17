package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.bombhunt.game.model.Grid;


public class GridPositionComponent extends Component {
    public Grid grid;
    public boolean snapToGrid = true;
    public boolean accumelate = false;
    public Vector2 accumelator = new Vector2();
    public int cellIndex = -1;
}
