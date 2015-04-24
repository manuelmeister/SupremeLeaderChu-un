package ninja.chuun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Arrays;

public class MapRenderer {

    private static final int FRAME_COLS = 8;
    private static final int FRAME_ROWS = 1;
    private static final float CHUUN_RATE = 0.075f;

    Map map;
    OrthographicCamera camera;
    FPSLogger fps = new FPSLogger();

    ShapeRenderer debugRenderer = new ShapeRenderer();

    SpriteCache mapCache;
    SpriteBatch batch = new SpriteBatch(5460);

    private float posX;
    private float posY;
    private float scale;

    Texture walkSheet;

    SpriteBatch spriteBatch;
    TextureRegion currentFrame;
    TextureRegion tile;
    TextureRegion spikes;

    int[][] blocks;

    float stateTime;
    Animation chuun_right;
    Animation chuun_left;
    Animation chuun_resting;



    public MapRenderer(Map map) {
        this.map = map;
        this.camera = new OrthographicCamera(24, 16);

        this.camera.position.set(map.chuun.pos.x, map.chuun.pos.y, 0);
        this.mapCache = new SpriteCache(this.map.tiles.length * this.map.tiles[0].length, false);
        this.blocks = new int[(int) (this.map.tiles.length / 24.0f)][(int) (this.map.tiles[0].length / 16.0f)];

        createAnimation();
        createBlocks();
    }

    private void createBlocks() {
        int width = map.tiles.length;
        int height = map.tiles[0].length;

        for (int blockX = 0; blockX < blocks.length; blockX++) {
            for (int blockY = 0; blockY < blocks[0].length; blockY++) {

                mapCache.beginCache();

                for (int x = blockX * 24; x < blockX * 24 + 24; x++) {
                    for (int y = blockY * 16; y < blockY * 16 + 16; y++) {

                        if (x > width) continue;
                        if (y > height) continue;
                        int posX = x;
                        int posY = height - y - 1;

                        if (map.tiles[x][y] == Map.TILE) mapCache.add(tile, posX, posY, 1, 1);
                        if (map.tiles[x][y] == Map.SPIKES) mapCache.add(spikes, posX, posY, 1, 1);
                    }
                }

                blocks[blockX][blockY] = mapCache.endCache();
            }
        }
        Gdx.app.debug("MapRenderer", "blocks created");
    }

    private void createAnimation() {
        this.tile = new TextureRegion(new Texture(Gdx.files.internal("tile32.png")));
        this.spikes = new TextureRegion(new Texture(Gdx.files.internal("bucket.png")));
        walkSheet = new Texture(Gdx.files.internal("chu-un.png"));

        TextureRegion[] chuunTexture = new TextureRegion(walkSheet).split(32, 32)[0];

        TextureRegion[] chuunTextureMirrored = new TextureRegion(walkSheet).split(32, 32)[0];
        for (TextureRegion textureRegion : chuunTextureMirrored) {
            textureRegion.flip(true, false);
        }

        chuun_right = new Animation(CHUUN_RATE, Arrays.copyOfRange(chuunTexture, 1, 8));
        chuun_left = new Animation(CHUUN_RATE, Arrays.copyOfRange(chuunTextureMirrored, 1, 8));

        chuun_resting = new Animation(0, chuunTexture[0]);

        spriteBatch = new SpriteBatch();
        stateTime = 0f;
    }

    Vector3 lerpTarget = new Vector3();

    //@Override
    public void render(float deltaTime) {
        map.chuun.updateState();

        camera.position.lerp(lerpTarget.set(map.chuun.pos, 0), 10f * deltaTime);
        camera.update();


        mapCache.setProjectionMatrix(camera.combined);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        stateTime += Gdx.graphics.getDeltaTime();

        mapCache.begin();

        for (int blockX = 0; blockX < 6; blockX++) {
            for (int blockY = 0; blockY < 4; blockY++) {
                mapCache.draw(blocks[blockX][blockY]);
            }
        }

        mapCache.end();

        spriteBatch.begin();
        renderChuun();
        spriteBatch.end();

        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Rectangle[] collisionHalo = map.chuun.collisionHalo;
        debugRenderer.setColor(new Color(0, 1, 0, 1));
        debugRenderer.rect(collisionHalo[0].x, collisionHalo[0].y, 1, 1);
        debugRenderer.setColor(new Color(0, 1, 1, 1));
        debugRenderer.rect(collisionHalo[1].x, collisionHalo[1].y, 1, 1);
        debugRenderer.setColor(new Color(1, 0, 0, 1));
        debugRenderer.rect(collisionHalo[2].x, collisionHalo[2].y, 1, 1);
        debugRenderer.setColor(new Color(1, 1, 1, 1));
        debugRenderer.rect(collisionHalo[3].x, collisionHalo[3].y, 1, 1);
        debugRenderer.setColor(new Color(1, 0, 1, 1));
        debugRenderer.rect(collisionHalo[4].x, collisionHalo[4].y, 1, 1);

        debugRenderer.end();

        fps.log();
    }

    private void renderChuun() {
        Animation animation = null;
        boolean loopAnimation = true;
        if (map.chuun.state == map.chuun.RUN) {
            if (map.chuun.dir == map.chuun.RIGHT) {
                animation = chuun_right;
            } else if (map.chuun.dir == map.chuun.LEFT) {
                animation = chuun_left;
            }
        } else {
            animation = chuun_resting;
        }
        currentFrame = animation.getKeyFrame(map.chuun.stateTime,loopAnimation);
        spriteBatch.draw(currentFrame,Gdx.graphics.getWidth()/2 - map.chuun.bounds.width*32/2,Gdx.graphics.getHeight()/2, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }

    public void dispose() {
        mapCache.dispose();
        batch.dispose();
        tile.getTexture().dispose();
    }
}
