package ninja.chuun;

import com.badlogic.gdx.math.Vector2;

public class Spawn {
    Map map;
    Vector2 pos;
    public Spawn(Map map, int x, int y) {
        this.map = map;
        this.pos = new Vector2(x,y);
    }
}
