package si.feri.rrizemljevid

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Logger
import si.feri.rrizemljevid.screen.IntroScreen

class MyGdxGame : Game() {
    // you MUST have ONLY ONE instance of the AssetManager and SpriteBatch in the game
    var assetManager: AssetManager? = null
        private set
    var batch: SpriteBatch? = null
        private set

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        assetManager = AssetManager()
        assetManager!!.logger.level = Logger.DEBUG
        batch = SpriteBatch()
        setScreen(IntroScreen(this))
    }

    override fun dispose() {
        assetManager!!.dispose()
        batch!!.dispose()
    }
}