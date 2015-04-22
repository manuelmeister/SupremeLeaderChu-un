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

public class IntroScreen implements Screen {

    Game game;

    public IntroScreen (Game game) {
        this.game = game;
    }

    private OrthographicCamera camera;
    private SpriteBatch batch;


    //TODO add intro texture
    //private Texture intro = new Texture()

    //Map map;

    private long startTime = System.nanoTime()/1000000000;

    //MapRenderer mapRenderer;
    ModelInstance instance;
    AnimationController controller;

    @Override
    public void show() {

        //load animations
        //Map	map = new Map();
        //mapRenderer = new MapRenderer(map);

        //load and set camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        //instantiate SpriteBatch
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());

        //Set Color to blue
        Gdx.gl.glClearColor(0, 0.8f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //mapRenderer.render(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)){
            game.setScreen(new GameScreen(game));
        }


        //Batch Render-Code
        batch.begin();
        //batch.draw(texture);
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
        //mapRenderer.dispose();
    }

    @Override
    public void dispose() {

    }


}
