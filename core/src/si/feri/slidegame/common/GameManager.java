package si.feri.slidegame.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import si.feri.slidegame.MyGdxGame;

public class GameManager {
    public static final GameManager INSTANCE = new GameManager();

    //
    //
    //

    private final Preferences PREFS;

    //
    //
    //

    private GameManager() {
        PREFS = Gdx.app.getPreferences(MyGdxGame.class.getSimpleName());
    }
}
