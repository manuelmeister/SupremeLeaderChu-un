package ninja.chuun;

import com.badlogic.gdx.math.Rectangle;

public class Trampolin {
    public Rectangle bounds = new Rectangle();

    public Trampolin(Map map, int x, int y) {
        this.bounds.x = x;
        this.bounds.y = y;
        this.bounds.width = this.bounds.height = 1;
    }
}
