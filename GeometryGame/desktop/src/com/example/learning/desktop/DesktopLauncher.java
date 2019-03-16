package com.example.learning.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.example.learning.LaserKittens;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Geometry";
		config.width = 630;
		config.height = 900;
		config.forceExit = false;
		new LwjglApplication(new LaserKittens(), config);
	}
}
