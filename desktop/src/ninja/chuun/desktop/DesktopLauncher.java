package ninja.chuun.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ninja.chuun.GameScreen;
import ninja.chuun.SupremeGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 1;
		new LwjglApplication(new SupremeGame(), config);

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
}
