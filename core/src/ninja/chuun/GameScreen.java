package ninja.chuun;

import com.badlogic.gdx.*;
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

	Game game;

	public GameScreen (Game game) {
		this.game = game;
	}

	//private Sprite chu_unImage;
	//private Sprite blackImage;
	//private Sound dropSound;
	//private Music rainMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	//private Rectangle chu_un;

	private double supremeSpeed;
	private double supremeGravitySpeed;

	Map map;

	//private Array<Rectangle> scientists;
	//private long lastScientistTime;
	//private long gravityTime;
	private long startTime = System.nanoTime()/1000000000;

	//private double gravityAcceleration = -500;
	//private double supremeAcceleration = 1000;
	//private double movementTimeStep = 0.001;

	MapRenderer mapRenderer;
	ModelInstance instance;
	AnimationController controller;

	int counter = 0;

	//private Rectangle floor;
	//private Rectangle wall;

	@Override
	public void show() {

		//load animations
		Map	map = new Map();
		mapRenderer = new MapRenderer(map);

		// load the images for the chu_un, 32 pixels
		//chu_unImage = new Texture(Gdx.files.internal("chu_un.png"));
		//Texture texture = new Texture(Gdx.files.internal("bucket.png"));
		//blackImage = new Sprite(texture);

		//load and set camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		//instantiate SpriteBatch
		batch = new SpriteBatch();


		/* old code (from testing)
		//Create SupremeRectangled
		chu_un = new Rectangle();
		chu_un.x = 50;
		chu_un.y = 100;
		//rectangle is smaller than Sprite -> better collision detection. Both sides 8 pixels times SCALE empty
		chu_un.width = map.chuun.getBounds().x;
		chu_un.height = map.chuun.getBounds().y;

		//Create Floor (same size as black Image)
		floor = new Rectangle();
		floor.x = 0;
		floor.y = 0;
		floor.width = 500;
		floor.height = 30;

		//Create Wall (same size as black Image)
		wall = new Rectangle();
		wall.x = 0;
		wall.y = 0;
		wall.width = 30;
		wall.height = 500;

		scientists = new Array<Rectangle>();
		spawnScientist();
		*/

	}

	@Override
	public void render(float delta) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());

//		System.out.println("Game time in sec: " + ((System.nanoTime() / 1000000000) - startTime));
//		System.out.println("Frame counter: "+counter++);
//		if (((System.nanoTime() / 1000000000) - startTime) != 0){
//			System.out.println("Average fps: "+(counter/((System.nanoTime() / 1000000000) - startTime)));
//		}
//		System.out.println("------------------------------------");
		//Set Color to blue
		Gdx.gl.glClearColor(0, 0.8f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mapRenderer.render(delta);



		/* Movement estimations ar now lovated inside the Chuun class


		// Mouse movements
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			chu_un.x = touchPos.x - 64 / 2;
		}

		if (!((Gdx.input.getX() < 0) || (Gdx.input.getX() > 800 - 64))){
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			chu_un.x = touchPos.x - 64 / 2;
		}

		//Gravity Movements
		boolean checkTimeMovements = false;
		if (gravityTime != ((System.nanoTime() / 1000000) - startTime)) {
			checkTimeMovements = true;
			gravityTime = ((System.nanoTime() / 1000000) - startTime);
		}


		//Estimate Gravity Speed
		if (checkTimeMovements){
			System.out.println("GravityTime: "+ gravityTime);
			chu_un.y -= movementTimeStep * (supremeGravitySpeed + movementTimeStep * gravityAcceleration / 2);
			supremeGravitySpeed += movementTimeStep * gravityAcceleration;
		}

		//Initiate Jump
		if(Gdx.input.isKeyPressed(Input.Keys.W)){
			supremeGravitySpeed = 10;
		}

		//free fall
		chu_un.y += supremeGravitySpeed;

		//stay on floor
		if (chu_un.overlaps(floor)) {
			chu_un.y = floor.y + floor.height;
			supremeGravitySpeed = 0;
		}




		// Key Movements
		boolean moved = false;
		if(Gdx.input.isKeyPressed(Input.Keys.A)){
			if (checkTimeMovements) {
				supremeSpeed -= movementTimeStep * supremeAcceleration;
			}
			moved = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			if (checkTimeMovements) {
				supremeSpeed += movementTimeStep * supremeAcceleration;
			}
			moved = true;

		}
		if (!moved){
			if (checkTimeMovements) {
				supremeSpeed /= 2;
				if (supremeSpeed<0.1){
					supremeSpeed = 0;
				}
			}
		}





		//max Speed
		if (supremeSpeed > 10) supremeSpeed = 10;
		if (supremeSpeed < -10) supremeSpeed = -10;



		//walk
		chu_un.x += supremeSpeed;

		if (chu_un.overlaps(wall)){
			chu_un.x = wall.x + wall.getWidth();
		}

		//stay in scene
		if(chu_un.x > Gdx.graphics.getWidth() - map.chuun.getBounds().x) chu_un.x = Gdx.graphics.getWidth() - map.chuun.getBounds().x;
		if(chu_un.x < 0) chu_un.x = 0;

		mapRenderer.Position((int) chu_un.x, (int) chu_un.y);
		mapRenderer.render();
		*/

		/*Iterator<Rectangle> iter = scientists.iterator();
		while(iter.hasNext()) {
			Rectangle scientist = iter.next();
			scientist.y -= 200 * Gdx.graphics.getDeltaTime();
			if(scientist.y + 64 < 0) iter.remove();
			if(scientist.overlaps(chu_un)) {
				iter.remove();
			}
		}*/



		if (map.chuun.bounds.overlaps(map.endDoor.bounds)) {
			//TODO embed game over screen
			System.out.println("Game over");//game.setScreen(new GameOverScreen(game));
		}

		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			//TODO embed main menu screen
			System.out.println("Main menu");//game.setScreen(new MainMenu(game));
		}

		//Batch Render-Code
		batch.begin();

		batch.end();

	}

	@Override
	public void resize(int width, int height) {

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