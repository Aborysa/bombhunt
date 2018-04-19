package com.bombhunt.game.model.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;

public class LabelComponent extends Component {
    public String label = "DEFAULT";
    public Vector3 position = Vector3.Zero;
}
