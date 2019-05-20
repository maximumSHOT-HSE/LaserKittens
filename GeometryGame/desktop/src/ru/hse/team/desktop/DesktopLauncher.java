package ru.hse.team.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.hse.team.LaserKittens;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Geometry";
		config.width = 720;
		config.height = 1000;
		config.forceExit = false;
		new LwjglApplication(new LaserKittens(null, null), config);
	}
}
