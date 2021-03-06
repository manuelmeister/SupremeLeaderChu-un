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
import java.util.Random;

public class MapRenderer {

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
    TextureRegion[] textureTiles;
    TextureRegion[] specialTiles;
    Random tileRandom;
    TextureRegion spike;
    TextureRegion nextLevel;
    TextureRegion endDoor;
    TextureRegion trampolin;

    int[][] blocks;

    float stateTime;
    Animation chuun_right;
    Animation chuun_left;
    Animation chuun_resting;
    Animation chuun_jump_right;
    Animation chuun_jump_left;
    Animation lava;
    private TextureRegion floortile;


    public MapRenderer(Map map) {
        this.map = map;
        this.camera = new OrthographicCamera(15, 10);
        this.camera.zoom = 1.5f;

        this.tileRandom = new Random();

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
                        Gdx.gl.glEnable(GL20.GL_BLEND);
                        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                        //TODO 1.05f bechause it had lines
                        if (map.tiles[x][y] == Map.TILE) mapCache.add(textureTiles[tileRandom.nextInt(textureTiles.length)], posX, posY, 1.05f, 1.05f);
                        if (map.tiles[x][y] == Map.FLOORTILE) mapCache.add(floortile, posX, posY, 1.01f, 1.01f);
                        //if (map.tiles[x][y] == Map.END)
                        //    mapCache.add(endDoor, posX, posY, 1, 1);
                    }
                }

                blocks[blockX][blockY] = mapCache.endCache();
            }
        }
        Gdx.app.debug("MapRenderer", "blocks created");
    }

    private void createAnimation() {
        this.textureTiles = new TextureRegion(new Texture(Gdx.files.internal("tile32.png"))).split(32, 32)[0];
        this.specialTiles = new TextureRegion(new Texture(Gdx.files.internal("specialtile32.png"))).split(32, 32)[0];
        this.floortile = this.specialTiles[0];
        this.spike = this.specialTiles[1];
        this.nextLevel = new TextureRegion(new Texture(Gdx.files.internal("door.png")));
        this.endDoor = new TextureRegion(new Texture(Gdx.files.internal("enddoor.png")));
        this.trampolin = new TextureRegion(new Texture(Gdx.files.internal("trampolin.png")));

        walkSheet = new Texture(Gdx.files.internal("sprites.png"));

        TextureRegion[] lavaTexture = new TextureRegion(new Texture(Gdx.files.internal("lava.png"))).split(32, 32)[0];

        TextureRegion[] chuunTexture = new TextureRegion(walkSheet).split(32, 32)[0];

        TextureRegion[] chuunTextureMirrored = new TextureRegion(walkSheet).split(32, 32)[0];
        for (TextureRegion textureRegion : chuunTextureMirrored) {
            textureRegion.flip(true, false);
        }

        chuun_right = new Animation(CHUUN_RATE, Arrays.copyOfRange(chuunTexture, 1, 8));
        chuun_left = new Animation(CHUUN_RATE, Arrays.copyOfRange(chuunTextureMirrored, 1, 8));

        chuun_jump_right = new Animation(CHUUN_RATE, chuunTexture[8]);
        chuun_jump_left = new Animation(CHUUN_RATE, chuunTextureMirrored[8]);

        chuun_resting = new Animation(0, chuunTexture[0]);

        lava = new Animation(0.1f,lavaTexture);

        spriteBatch = new SpriteBatch();
        stateTime = 0f;

    }

    Vector3 lerpTarget = new Vector3();

    //@Override
    public void render(float deltaTime) {
        map.chuun.updateState();

        int tile = map.tiles[(int)map.chuun.bounds.x][map.tiles[0].length - 1 - (int)Math.floor(map.chuun.bounds.y)];
        if (tile == Map.ZOOM_NORMAL){
            camera.zoom = 1.5f;
        }else if (tile == Map.ZOOM2){
            camera.zoom = 2.5f;
        }else if (tile == Map.ZOOM3){
            camera.zoom = 3.5f;
        }

        camera.position.lerp(lerpTarget.set(map.chuun.pos.x,map.chuun.pos.y+(map.chuun.bounds.height*4), 0), 10f * deltaTime);
        camera.update();


        mapCache.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        stateTime += Gdx.graphics.getDeltaTime();

        mapCache.begin();

        for (int[] block : blocks) {
            for (int i : block) {
                mapCache.draw(i);
            }
        }

        mapCache.end();

        spriteBatch.begin();

        renderTrampolins();
        renderChuun();
        renderLava();
        renderSpikes();


        if (map.nextLevel != null) {
            spriteBatch.draw(this.nextLevel, map.nextLevel.pos.x, map.nextLevel.pos.y, 1, 1);
        } else {
            spriteBatch.draw(this.endDoor, map.endDoor.bounds.x, map.endDoor.bounds.y, 1, 1);
        }





        spriteBatch.end();

        //debugRenderer();

        fps.log();
    }

    private void renderSpikes() {
        for (Spike spike : map.spikes) {
            spriteBatch.draw(this.spike,spike.pos.x,spike.pos.y,1,1);
        }
    }

    private void renderTrampolins() {
        for (Trampolin trampolin : map.trampolins) {
            spriteBatch.draw(this.trampolin,trampolin.bounds.x,trampolin.bounds.y,1,1);
        }
    }

    private void renderLava() {
        for (Lava lava : map.lavas) {
            spriteBatch.draw(this.lava.getKeyFrame(Lava.stateTime++),lava.pos.x,lava.pos.y,1,1);
        }
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
        } else if (map.chuun.state == map.chuun.JUMP) {
            if (map.chuun.dir == map.chuun.RIGHT) {
                animation = chuun_jump_right;
            } else if (map.chuun.dir == map.chuun.LEFT) {
                animation = chuun_jump_left;
            }
        } else {
            animation = chuun_resting;
        }
        currentFrame = animation.getKeyFrame(map.chuun.stateTime,loopAnimation);
        //spriteBatch.draw(currentFrame,Gdx.graphics.getWidth()/2 - map.chuun.bounds.width*32/2,Gdx.graphics.getHeight()/2, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        spriteBatch.draw(currentFrame,map.chuun.pos.x,map.chuun.pos.y, 1, 1);
    }

    private void debugRenderer(){
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Rectangle[] collisionHalo = map.chuun.collisionHalo;
        debugRenderer.setColor(new Color(0, 0.7f, 0.5f, 1));
        debugRenderer.rect(map.chuun.bounds.x, map.chuun.bounds.y, map.chuun.bounds.width, map.chuun.bounds.height);
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
    }

    public void dispose() {
        mapCache.dispose();
        batch.dispose();
        for (TextureRegion tile : textureTiles) {
            tile.getTexture().dispose();
        }
    }
}
