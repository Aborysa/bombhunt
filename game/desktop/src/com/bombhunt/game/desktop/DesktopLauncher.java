package com.bombhunt.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bombhunt.game.BombHunt;

public class DesktopLauncher {
  public static void main (String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.vSyncEnabled = false;
    config.foregroundFPS = 0;
    config.backgroundFPS = 1;
    new LwjglApplication(new BombHunt(), config);
  }
}
