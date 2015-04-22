package ninja.chuun;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Arrays;

public class IntroScreen implements Screen {

    Game game;

    public IntroScreen (Game game) {
        this.game = game;
    }

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Animation chu_unAnimation;
    float stateTime = 0f;

    private Texture introwall = new Texture(Gdx.files.internal("introwall.png"));
    private TextureRegion introwallRegion = new TextureRegion(introwall);
    private Texture introfloor = new Texture(Gdx.files.internal("introfloor.png"));
    private TextureRegion introfloorRegion = new TextureRegion(introfloor);
    private Texture introchu_un = new Texture(Gdx.files.internal("introchu_un.png"));
    private TextureRegion introchu_unRegion = new TextureRegion(introchu_un);
    private Texture introspeech = new Texture(Gdx.files.internal("introspeech.png"));
    private TextureRegion introspeechRegion = new TextureRegion(introspeech);

    //Map map;

    private float x = 0;

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

        Texture walkSheet = new Texture(Gdx.files.internal("chu-un.png"));

        TextureRegion[] chuunTexture = TextureRegion.split(walkSheet, walkSheet.getWidth() / 8, walkSheet.getHeight() / 1)[0];

        chu_unAnimation = new Animation(0.25f, Arrays.copyOfRange(chuunTexture, 1, 8));

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

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = chu_unAnimation.getKeyFrame(stateTime, true);

        x += 30 * delta;
        if (x > Gdx.graphics.getWidth()){
            x = -currentFrame.getRegionWidth() - 200 ;
        }

        //currentFrame.setRegionWidth(currentFrame.getRegionWidth());
        //currentFrame.setRegionHeight(currentFrame.getRegionHeight());

        //Batch Render-Code
        batch.begin();
        batch.draw(introwallRegion, 0, 0);
        batch.draw(introfloorRegion, 0, 0);
        batch.draw(introchu_unRegion, Gdx.graphics.getWidth()/2 - introchu_unRegion.getRegionWidth()/2, Gdx.graphics.getHeight()/4*3);
        batch.draw(currentFrame, x, introfloor.getHeight(), currentFrame.getRegionWidth()*4, currentFrame.getRegionHeight()*4);
        batch.draw(introspeechRegion, x+currentFrame.getRegionWidth()*2, introfloor.getHeight()+currentFrame.getRegionHeight()*4, introspeechRegion.getRegionWidth(), introspeechRegion.getRegionHeight());
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
