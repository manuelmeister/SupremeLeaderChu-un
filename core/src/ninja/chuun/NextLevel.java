package ninja.chuun;

import com.badlogic.gdx.math.Vector2;

public class NextLevel {
    Vector2 pos;
    Map map;
    public NextLevel(Map map, int x, int y) {
        this.map = map;
        pos = new Vector2(x,y);
    }
}
