package ninja.chuun;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
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
    private int startLevel = 7;
    private Music supremeMusic;
    private Music gong;
    private Music pressanykey;
    private float introTime = 0;

    public IntroScreen (Game game) {
        this.game = game;
        supremeMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/music.mp3"));
        supremeMusic.setVolume(0.005f);
        supremeMusic.setLooping(true);
        supremeMusic.play();
        gong = Gdx.audio.newMusic(Gdx.files.internal("sound/gong.mp3"));
        gong.setVolume(0.1f);
        pressanykey = Gdx.audio.newMusic(Gdx.files.internal("sound/pressanykey.mp3"));
        pressanykey.setVolume(0.1f);
        pressanykey.play();
    }

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Animation chu_unAnimation;
    float stateTime = 0f;

    private Texture background = new Texture(Gdx.files.internal("GUI/background.png"));
    private TextureRegion backgroundRegion = new TextureRegion(background);
    private Texture logo = new Texture(Gdx.files.internal("logo.png"));
    private TextureRegion logoRegion = new TextureRegion(logo);
    private Texture introspeech = new Texture(Gdx.files.internal("GUI/introspeech.png"));
    private TextureRegion introspeechRegion = new TextureRegion(introspeech);
    private Texture subtitle = new Texture(Gdx.files.internal("GUI/subtitle.png"));
    private TextureRegion subtitleRegion = new TextureRegion(subtitle);

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
        camera = new OrthographicCamera(160,117);
        //camera.setToOrtho(false, 800, 480);

        //instantiate SpriteBatch
        batch = new SpriteBatch();

        Texture walkSheet = new Texture(Gdx.files.internal("sprites.png"));

        TextureRegion[] chuunTexture = new TextureRegion(walkSheet).split(32, 32)[0];

        chu_unAnimation = new Animation(0.25f, Arrays.copyOfRange(chuunTexture, 1, 8));

    }

    @Override
    public void render(float delta) {
        delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());

        introTime += Gdx.graphics.getDeltaTime();
        if (introTime > 6){
            pressanykey.play();
            introTime = 0;
        }

        //Set Color to blue
        Gdx.gl.glClearColor(0, 0.8f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //mapRenderer.render(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && !Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            supremeMusic.stop();
            gong.play();
            //base.play();
            game.setScreen(new GameScreen(game, startLevel));
        }

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = chu_unAnimation.getKeyFrame(stateTime, true);

        x += 30 * delta;
        if (x > Gdx.graphics.getWidth()){
            x = -currentFrame.getRegionWidth() - 200;
        }

        //currentFrame.setRegionWidth(currentFrame.getRegionWidth());
        //currentFrame.setRegionHeight(currentFrame.getRegionHeight());

        //Batch Render-Code
        batch.begin();
        batch.draw(backgroundRegion, 0, 0);
        batch.draw(logoRegion, Gdx.graphics.getWidth()/2-logo.getWidth()/2, Gdx.graphics.getHeight()/4*3-logo.getHeight()/2);
        batch.draw(subtitleRegion, Gdx.graphics.getWidth()/2-subtitle.getWidth()/2, Gdx.graphics.getWidth()/2-subtitle.getHeight()/2);
        //batch.draw(introfloorRegion, 0, 0);
        //batch.draw(introchu_unRegion, Gdx.graphics.getWidth()/2 - introchu_unRegion.getRegionWidth()/2, Gdx.graphics.getHeight()/4*3);
        //batch.draw(currentFrame, x, introfloor.getHeight(), currentFrame.getRegionWidth()*4, currentFrame.getRegionHeight()*4);
        //batch.draw(introspeechRegion, x+currentFrame.getRegionWidth()*2, introfloor.getHeight()+currentFrame.getRegionHeight()*4, introspeechRegion.getRegionWidth(), introspeechRegion.getRegionHeight());
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
