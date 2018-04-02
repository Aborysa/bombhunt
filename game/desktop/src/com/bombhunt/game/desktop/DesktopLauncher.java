package com.bombhunt.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.services.networking.PlayAdapter;

public class DesktopLauncher {
  public static void main (String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.vSyncEnabled = false;
    config.foregroundFPS = 0;
    config.backgroundFPS = 0;
    config.height = 800;
    config.width = 800;



    new LwjglApplication(new BombHunt(new PlayAdapter()), config);
  }
}
