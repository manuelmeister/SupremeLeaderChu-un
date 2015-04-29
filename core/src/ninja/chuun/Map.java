package ninja.chuun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Map {

    static final int EMPTY = 0;
    static final int TILE = 0xffffff;
    static final int SPAWN = 0xff0000;
    static final int SPIKES = 0x00ff00;
    static final int SCIENTIST = 0x0000ff;
    static final int COLLECTABLE = 0xffff00;
    static final int LAVA = 0xff4400;
    static final int NEXTLEVEL = 0xff22ff;
    static final int END = 0xff00ff;

    int[][] tiles;
    Pixmap pixelmap;
    Chuun chuun;
    Spawn spawn;
    EndDoor endDoor;
    ArrayList<Scientist> scientists = new ArrayList<Scientist>();
    ArrayList<Spike> spikes = new ArrayList<Spike>();
    ArrayList<Collectable> collectables = new ArrayList<Collectable>();
    ArrayList<Lava> lavas = new ArrayList<Lava>();

    public Map() {
        loadMap(2);
    }

    private void loadMap(int level) {
        pixelmap = new Pixmap(Gdx.files.internal("levels/level" + level + ".png"));
        tiles = new int[pixelmap.getWidth()][pixelmap.getHeight()];

        for (int x = 0; x < pixelmap.getWidth(); x++) {
            for (int y = 0; y < pixelmap.getHeight(); y++) {

                int pixel = (pixelmap.getPixel(x,y) >>> 8) & 0xffffff;
                if (pixel == SPAWN) {
                    spawn = new Spawn(this, x, pixelmap.getHeight() - 1 - y);
                    chuun = new Chuun(this, new Vector2(spawn.pos.x, spawn.pos.y));
                    chuun.state = Chuun.SPAWN;

                } else if (pixel == SCIENTIST) {
                    Scientist scientist = new Scientist(this, x, pixelmap.getHeight() - 1 - y);
                    scientists.add(scientist);

                } else if (pixel == COLLECTABLE) {
                    Collectable collectable = new Collectable(this, x, pixelmap.getHeight() - 1 - y);
                    collectables.add(collectable);

                }else if (pixel == LAVA) {
                    Lava lava = new Lava(this, x, pixelmap.getHeight() - 1 - y);
                    lavas.add(lava);

                } else if (pixel == END) {
                    endDoor = new EndDoor(this, x, pixelmap.getHeight() - 1 - y);

                }
                tiles[x][y] = pixel;

            }
        }

    }

    public boolean isDeadly(int tile) {
        return (tile == SPIKES || tile == LAVA);
    }

    public boolean isWin(int tile) {
        return tile == END;
    }

    public void update(float delta) {
        if (chuun.state == chuun.DEAD){
            chuun = new Chuun(this, new Vector2(spawn.pos.x, spawn.pos.y));
            chuun.state = Chuun.SPAWN;
        }
        chuun.update(delta);
    }

    public boolean isNextLevel(int frontTile) {
        return frontTile == NEXTLEVEL;
    }
}
