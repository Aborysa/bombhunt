package com.bombhunt.game.model.ecs.factories;

import com.bombhunt.game.services.networking.Message;

/**
 * Created by samuel on 19/04/18.
 */

public interface INetworkFactory {

    public int createFromMessage(Message message);
    public Message pushToNetwork(Message m, int e);

}
