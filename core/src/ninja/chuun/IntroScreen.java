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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;

public class IntroScreen implements Screen {

    Game game;
    private int startLevel = 6;
    private Music supremeMusic;
    private Music gong;
    private Music pressanykey;
    private float introTime = 0;
    private float chu_un_x = -50f;
    private Texture background;
    private TextureRegion backgroundRegion;
    private Texture logo;
    private TextureRegion logoRegion;
    private Texture introspeech;
    private TextureRegion introspeechRegion;
    private Texture subtitle;
    private TextureRegion subtitleRegion;
    private Texture pavement;
    private TextureRegion pavementRegion;

    public IntroScreen(Game game) {
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


    //Map map;

    private float x = 0;

    private long startTime = System.nanoTime() / 1000000000;

    //MapRenderer mapRenderer;
    ModelInstance instance;
    AnimationController controller;

    @Override
    public void show() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("GUI/background.png"));
        pavement = new Texture(Gdx.files.internal("GUI/pavement.png"));
        pavementRegion = new TextureRegion(pavement);
        backgroundRegion = new TextureRegion(background);
        logo = new Texture(Gdx.files.internal("logo.png"));
        logoRegion = new TextureRegion(logo);
        introspeech = new Texture(Gdx.files.internal("GUI/introspeech.png"));
        introspeechRegion = new TextureRegion(introspeech);
        subtitle = new Texture(Gdx.files.internal("GUI/subtitle.png"));
        subtitleRegion = new TextureRegion(subtitle);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, 160, 117);
        Texture walkSheet = new Texture(Gdx.files.internal("sprites.png"));

        TextureRegion[] chuunTexture = new TextureRegion(walkSheet).split(32, 32)[0];

        chu_unAnimation = new Animation(0.15f, Arrays.copyOfRange(chuunTexture, 1, 8));

    }

    @Override
    public void render(float delta) {
        delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());

        introTime += Gdx.graphics.getDeltaTime();
        if (introTime > 6) {
            pressanykey.play();
            introTime = 0;
        }

        //Set Color to blue
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //mapRenderer.render(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) && !Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            supremeMusic.stop();
            gong.play();
            //base.play();
            game.setScreen(new GameScreen(game, startLevel));
        }

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = chu_unAnimation.getKeyFrame(stateTime, true);

        float width = backgroundRegion.getRegionWidth()/2;
        float height = backgroundRegion.getRegionHeight()/2;

        if (chu_un_x > (backgroundRegion.getRegionWidth() + currentFrame.getRegionWidth())){
            chu_un_x = -currentFrame.getRegionWidth();
        }

        //Batch Render-Code
        batch.begin();
        batch.draw(backgroundRegion, 0, 0);
        batch.draw(logoRegion, width-logoRegion.getRegionWidth()/2, height-logoRegion.getRegionHeight()/2 + height/2f);
        batch.draw(subtitleRegion, width-subtitleRegion.getRegionWidth()/2, height-subtitleRegion.getRegionHeight()/2 + height/5);
        batch.draw(pavementRegion,0,0);
        batch.draw(currentFrame, chu_un_x += 0.25f, pavementRegion.getRegionHeight());
        batch.draw(introspeechRegion, chu_un_x+currentFrame.getRegionWidth()/2, pavementRegion.getRegionHeight()+(currentFrame.getRegionHeight()*0.8f),30f,13f);
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
