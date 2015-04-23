package ninja.chuun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.text.DecimalFormat;

public class Chuun {
    final String[] states = {"IDLE","RUN","JUMP","DYING","SPAWN"};
    static final byte IDLE = 0;
    static final byte RUN = 1;
    static final byte JUMP = 3;
    static final byte DYING = 4;
    static final byte SPAWN = 5;

    static final byte LEFT = -1;
    static final byte RIGHT = 1;

    Vector2 pos = new Vector2();
    Vector2 accel = new Vector2();
    Vector2 vel = new Vector2();

    static final float GRAVITY = 9.81f;
    static final float MAX_VEL = 6f;
    static final float DAMP = 0.50f;
    static final float JUMP_VELOCITY = 7f;

    float stateTime = 0;

    Map map;

    public Rectangle bounds = new Rectangle();
    static final Vector2 SCALE = new Vector2(1, 1);

    boolean grounded = false;

    private float Acceleration = 20f;

    Rectangle[] collisionHalo = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

    byte state = SPAWN;
    byte dir = LEFT;

    public Chuun(Map map, Vector2 pos) {
        this.map = map;
        this.pos = pos;
        bounds.width = 0.5f;
        bounds.height = 1f;
        bounds.x = pos.x + 0.25f;
        bounds.y = pos.y;

        this.state = SPAWN;
        this.dir = LEFT;
    }

    public void update(float deltaTime){
        updateState();

        accel.y = -GRAVITY;
        accel.scl(deltaTime);
        vel.add(accel);

        if (accel.x == 0) vel.x *= DAMP;
        if (vel.x > MAX_VEL) vel.x = MAX_VEL;
        if (vel.x < -MAX_VEL) vel.x = -MAX_VEL;

        vel.scl(deltaTime);

        tryMove();

        vel.scl(1.0f / deltaTime);

        if (state == SPAWN) {
            if (stateTime > 0.4f) {
                state = IDLE;
            }
        }

        if (state == DYING) {
            if (stateTime > 0.4f) {
                state = SPAWN;
            }
        }

        stateTime += deltaTime;
    }

    public void updateState() {
        boolean movement = false;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            this.dir = LEFT;
            movement = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            this.dir = RIGHT;
            movement = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) && this.state != JUMP) {
            this.state = JUMP;
            vel.y = JUMP_VELOCITY;
            movement = true;
            grounded = false;
        }
        if (movement) {
            if (state != JUMP) state = RUN;
            accel.x = Acceleration * dir;
        } else {
            if (state != JUMP) state = IDLE;
            accel.x = 0;
        }
    }


    private void tryMove () {
        bounds.x += vel.x;
        setCollidableRects();
        for (Rectangle rect : collisionHalo) {
            if (bounds.overlaps(rect)) {
                if (vel.x < 0)
                    bounds.x = rect.x + rect.width + 0.01f;
                else
                    bounds.x = rect.x - bounds.width - 0.01f;
                vel.x = 0;
            }
        }

        bounds.y += vel.y;
        setCollidableRects();
        for (Rectangle rect : collisionHalo) {
            if (bounds.overlaps(rect)) {
                if (vel.y < 0) {
                    bounds.y = rect.y + rect.height + 0.01f;
                    grounded = true;
                    if (state != DYING && state != SPAWN) state = Math.abs(accel.x) > 0.1f ? RUN : IDLE;
                } else
                    bounds.y = rect.y - bounds.height - 0.01f;
                vel.y = 0;
            }
        }

        pos.x = bounds.x - 0.2f;
        pos.y = bounds.y;
        String log = "Pos:" +dv(pos) + "\t| Vel:" + dv(vel) + "\t| Accel:" + dv(accel) + "\t| Dir:" + dir + "\t| State:" + states[state] + "\t| StateTime:" + stateTime;
        Gdx.app.debug("SupremeGame",log);
    }

    /**
     * Nicer Vector2 output
     * @param v
     * @return String
     */
    public String dv(Vector2 v){
        DecimalFormat df = new DecimalFormat("#.0000000000");
        return "x:" + df.format(v.x) + ", y:" + df.format(v.y);
    }

    public void setCollidableRects(){
        Vector2 bottomLeft = new Vector2((int)bounds.x, (int)Math.floor(bounds.y));
        Vector2 bottomRight = new Vector2((int)(bounds.x + bounds.width), (int)Math.floor(bounds.y));
        Vector2 topRight = new Vector2((int)(bounds.x + bounds.width), (int)(bounds.y + bounds.height));
        Vector2 topLeft = new Vector2((int)bounds.x, (int)(bounds.y + bounds.height));

        int[][] tiles = map.tiles;
        int bodyTile = tiles[(int)bottomLeft.x][map.tiles[0].length - 1 - (int)bottomLeft.y];
        int frontTile = tiles[(int)bottomRight.x][map.tiles[0].length - 1 - (int)bottomRight.y];
        int frontUpperTile = tiles[(int)topRight.x][map.tiles[0].length - 1 - (int)topRight.y];
        int bodyUpperTile = tiles[(int)topLeft.x][map.tiles[0].length - 1 - (int)topLeft.y];

        if (state != DYING && (map.isDeadly(bodyTile) || map.isDeadly(frontTile) || map.isDeadly(frontUpperTile) || map.isDeadly(bodyUpperTile))) {
            state = DYING;
            stateTime = 0;
        }


        //TODO evtl. 1, 1 anpassen zu 16*this.SCALE, 32*this.SCALE

        if (bodyTile == Map.TILE)
            collisionHalo[0].set((int)bottomLeft.x, (int)bottomLeft.y, 1, 1);
        else
            collisionHalo[0].set(-1, -1, 0, 0);
        if (frontTile == Map.TILE)
            collisionHalo[1].set((int)bottomRight.x, (int)bottomRight.y, 1, 1);
        else
            collisionHalo[1].set(-1, -1, 0, 0);
        if (frontUpperTile == Map.TILE)
            collisionHalo[2].set((int)topRight.x, (int)topRight.y, 1, 1);
        else
            collisionHalo[2].set(-1, -1, 0, 0);
        if (bodyUpperTile == Map.TILE)
            collisionHalo[3].set((int)topLeft.x, (int)topLeft.y, 1, 1);
        else
            collisionHalo[3].set(-1, -1, 0, 0);

        collisionHalo[4].set(-1, -1, 0, 0);
    }

    public Vector2 getBounds(){
        return SCALE;
    }
}
