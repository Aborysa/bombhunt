package com.bombhunt.game.model.ecs.factories;

import com.artemis.World;

/**
 * Created by samuel on 19/04/18.
 */

public interface INetworkFactory {

    void setWorld(World world);

    int createFromMessage(String message);
}
