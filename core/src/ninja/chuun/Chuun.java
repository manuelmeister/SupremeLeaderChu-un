package ninja.chuun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Chuun {
    static final byte IDLE = 0;
    static final byte RUN = 1;
    static final byte JUMP = 3;
    static final byte DYING = 4;
    static final byte SPAWN = 5;

    static final byte LEFT = -1;
    static final byte RIGHT = 1;

    Vector2 pos = new Vector2(50,50);

    float stateTime = 0;

    Map map;

    public Rectangle bounds = new Rectangle();
    static final Vector2 SCALE = new Vector2(1, 1);

    private long gravityTime;

    private Vector2 supremeSpeed = new Vector2(0, 0);
    private float gravAcceleration = 10;
    private float Acceleration = 20;
    private Vector2 supremeAcceleration = new Vector2(0, 0); //wVector2(1000, -500);
    private double movementTimeStep = 0.001;
    private boolean jumping;

    private long startTime = System.nanoTime()/1000000000;

    Rectangle[] collisionHalo = {new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()};

    byte state = SPAWN;
    byte dir = LEFT;

    public Chuun(Map map, float x, float y) {
        this.map = map;

        //8 empty pixels on the left of original Sprite
        pos.x = 6;//x;
        pos.y = 9;//y;
        //bouds of original Sprite ar 16*32w
        bounds.width = 1*0.5f*this.SCALE.x;
        bounds.height = 1*this.SCALE.y;
        bounds.x = pos.x + 8;
        bounds.y = pos.y;

        this.state = SPAWN;
        this.dir = LEFT;
    }

    public void update(){
        updateState();
        //tryMove();

        //Gravity Movements (Check if time in microseconds has changed)
        boolean checkTimeMovements = false;
        if (gravityTime != ((System.nanoTime() / 1000000) - startTime)) {
            checkTimeMovements = true;
            gravityTime = ((System.nanoTime() / 1000000) - startTime);
        }



        /*

        Key Movements

         */

        boolean moved = false;
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            if (checkTimeMovements) {
                supremeSpeed.x -= movementTimeStep*100*Acceleration; //movementTimeStep * supremeAcceleration.x;
            }
            moved = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (checkTimeMovements) {
                supremeSpeed.x += movementTimeStep*100*Acceleration; //movementTimeStep * supremeAcceleration.x;
            }
            moved = true;

        }
        //Initiate Jump
        if(Gdx.input.isKeyPressed(Input.Keys.W) && state != JUMP){
            supremeSpeed.y = 10;
            System.out.println("JUMP!! jump velocity:  "+supremeSpeed.y);
            jumping = true;
        }




        /*

        Automatic Movements

         */

        //Friction (slowing down)
        if (!moved){
            if (checkTimeMovements) {
                if (supremeSpeed.x > 0) supremeSpeed.x -= 1;
                if (supremeSpeed.x < 0) supremeSpeed.x += 1;

                if ((supremeSpeed.x<1) && (supremeSpeed.x>-1)){
                    supremeSpeed.x = 0;
                    supremeAcceleration.x = 0;
                }
            }
        }

        //Free fall
        if (checkTimeMovements){
            //System.out.println("GravityTime: "+ gravityTime);
            supremeAcceleration.y = -gravAcceleration;//movementTimeStep * (supremeSpeed.y + movementTimeStep * supremeAcceleration.y / 2);
            supremeSpeed.y += movementTimeStep * supremeAcceleration.y * 50;
            System.out.println("Freefall velocity:    "+supremeSpeed.y);
        }




        /*

        Physical Boundaries

         */

        //max Speed
        if (supremeSpeed.x > 6) supremeSpeed.x = 6;
        if (supremeSpeed.x < -6) supremeSpeed.x = -6;
        if (supremeSpeed.y > 20) supremeSpeed.y = 20;
        if (supremeSpeed.y < -20) supremeSpeed.y = -20;


        //MOVE (walk, jump and fall)
        bounds.y += supremeSpeed.y;
        bounds.x += supremeSpeed.x;


        //stay in stage
        if(bounds.x > Gdx.graphics.getWidth() - bounds.width - 42) bounds.x = Gdx.graphics.getWidth() - bounds.width - 42;
        if(bounds.x < 10) bounds.x = 10;

        //stay on floor
        //TODO take this out! supposed to work with collision detection
        //if(bounds.y > Gdx.graphics.getHeight() - bounds.height - 10) {
        //    bounds.y = Gdx.graphics.getHeight() - bounds.height;
        //}
        if(bounds.y < 10) {
            bounds.y = 10;
            //supremeSpeed.y = 0;
        }

        pos.x = bounds.x;
        pos.y = bounds.y;

        System.out.println("x: "+ bounds.x+ " ;  y: "+ bounds.y+" ;  \taccel: "+supremeAcceleration.x+" ; "+supremeAcceleration.y+" ;  \tvel: "+supremeSpeed.x+" ; "+supremeSpeed.y);


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
        if (jumping) {
            this.state = JUMP;
            movement = true;
        }
        if (movement) {
            this.state = RUN;
        } else {
            this.state = IDLE;
        }
    }


    private void tryMove () {
        bounds.x += supremeSpeed.x;
        setCollidableRects();
        for (int i = 0; i < collisionHalo.length; i++) {
            Rectangle rect = collisionHalo[i];
            if (bounds.overlaps(rect)) {
                if (supremeSpeed.x < 0)
                    bounds.x = rect.x + rect.width + 0.01f;
                else
                    bounds.x = rect.x - bounds.width - 0.01f;
                supremeSpeed.x = 0;
            }
        }

        bounds.y += supremeSpeed.y;
        setCollidableRects();
        for (int i = 0; i < collisionHalo.length; i++) {
            Rectangle rect = collisionHalo[i];
            if (bounds.overlaps(rect)) {
                if (supremeSpeed.y < 0) {
                    bounds.y = rect.y + rect.height + 0.01f;
                    jumping = false;
                    if (state != DYING && state != SPAWN) state = Math.abs(supremeAcceleration.x) > 0.1f ? RUN : IDLE;
                } else
                    bounds.y = rect.y - bounds.height - 0.01f;
                supremeSpeed.y = 0;
            }
        }

        pos.x = bounds.x - 0.2f;
        pos.y = bounds.y;
    }



    public void setCollidableRects(){
        Vector2 bottomLeft = new Vector2(bounds.x, bounds.y);
        Vector2 bottomRight = new Vector2((bounds.x + bounds.width), bounds.y);
        Vector2 topRight = new Vector2((bounds.x + bounds.width), (bounds.y + bounds.height));
        Vector2 topLeft = new Vector2(bounds.x, (bounds.y + bounds.height));

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
