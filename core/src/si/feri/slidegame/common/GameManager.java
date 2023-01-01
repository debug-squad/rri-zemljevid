package si.feri.slidegame.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import si.feri.slidegame.MyGdxGame;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
