package si.feri.rrizemljevid.common

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import si.feri.rrizemljevid.MyGdxGame

class GameManager private constructor() {
    //
    //
    //
    private val PREFS: Preferences

    //
    //
    //
    init {
        PREFS = Gdx.app.getPreferences(MyGdxGame::class.java.simpleName)
    }

    companion object {
        val INSTANCE = GameManager()
    }
}