package ninja.chuun;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {

	public GameScreen (Game game, int level) {
		this.game = game;
		this.level = level;
	}

	Game game;
	int level;
	Map map;
	MapRenderer mapRenderer;
	private Music supremeMusic;

	@Override
	public void show() {
		map = new Map(level);
		mapRenderer = new MapRenderer(map);
		supremeMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/music.mp3"));
		supremeMusic.setVolume(0.005f);
		supremeMusic.setLooping(true);
		supremeMusic.play();
	}

	@Override
	public void render(float delta) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());

		if (map.chuun.state == map.chuun.WIN){
			game.setScreen(new WinScreen(game));
		}

		map.update(delta);

		//Set Color to grey
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mapRenderer.render(delta);


		if(map.endDoor != null){
			if (map.chuun.bounds.overlaps(map.endDoor.bounds)) {
				//TODO embed game over screen
				game.setScreen(new WinScreen(game));
				System.out.println("Game over");//game.setScreen(new GameOverScreen(game));
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			//TODO embed main menu screen
			game.setScreen(new IntroScreen(game));
			System.out.println("Main menu");//game.setScreen(new MainMenu(game));
		}
	}

	@Override
	public void resize(int width, int height) {
		mapRenderer.camera.translate(width,height);
		mapRenderer.camera.zoom = 2f;
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		Gdx.app.debug("SupremeLeaderChu-un", "dispose game screen");
		mapRenderer.dispose();
	}

	@Override
	public void dispose() {

	}


}
