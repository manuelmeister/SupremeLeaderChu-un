package ninja.chuun;

import com.badlogic.gdx.Game;

public class SupremeGame extends Game{

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}
