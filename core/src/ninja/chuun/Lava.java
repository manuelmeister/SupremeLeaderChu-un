package ninja.chuun;

import com.badlogic.gdx.math.Vector2;

public class Lava {

    static int stateTime = 0;
    Vector2 pos;

    public Lava(Map map, int x, int y) {
        pos = new Vector2(x,y);
    }
}
