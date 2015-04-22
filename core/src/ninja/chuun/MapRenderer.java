package ninja.chuun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import java.util.Arrays;

public class MapRenderer {

    private static final int FRAME_COLS = 8;
    private static final int FRAME_ROWS = 1;
    private static final float CHUUN_RATE = 0.075f;

    Map map;
    OrthographicCamera camera;
    FPSLogger fps = new FPSLogger();

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
        this.mapCache = new SpriteCache(map.tiles.length * map.tiles[0].length, false);
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
                        //if (map.tiles[x][y] == Map.SPIKES) mapCache.add(spikes, posX, posY, 1, 1);
                    }
                }

                blocks[blockX][blockY] = mapCache.endCache();
            }
        }
        Gdx.app.debug("MapRenderer", "blocks created");
    }

    private void createAnimation() {
        this.tile = new TextureRegion(new Texture(Gdx.files.internal("tile.png")), 0, 0, 16, 16);
        walkSheet = new Texture(Gdx.files.internal("chu-un.png"));

        TextureRegion[] chuunTexture = TextureRegion.split(walkSheet, walkSheet.getWidth() / FRAME_COLS, walkSheet.getHeight() / FRAME_ROWS)[0];

        TextureRegion[] chuunTextureMirrored = TextureRegion.split(walkSheet, walkSheet.getWidth() / FRAME_COLS, walkSheet.getHeight() / FRAME_ROWS)[0];
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

        camera.position.lerp(lerpTarget.set(map.chuun.pos, 0), 2f * deltaTime);
        camera.update();

        mapCache.setProjectionMatrix(camera.combined);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stateTime += Gdx.graphics.getDeltaTime();

        spriteBatch.begin();
        renderChuun();
        spriteBatch.end();

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
        batch.draw(currentFrame,map.chuun.pos.x, map.chuun.pos.y, currentFrame.getRegionWidth(), currentFrame.getRegionWidth());
    }

    public void dispose() {
        mapCache.dispose();
        batch.dispose();
        tile.getTexture().dispose();
    }
}
