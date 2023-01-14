package si.feri.rrizemljevid.screen

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import si.feri.rrizemljevid.MyGdxGame
import si.feri.rrizemljevid.assets.AssetDescriptors
import si.feri.rrizemljevid.config.GameConfig
import si.feri.rrizemljevid.screen.map.MapScreen

class IntroScreen(private val game: MyGdxGame) : ScreenAdapter() {
    private val assetManager: AssetManager?
    private var viewport: Viewport? = null
    private var gameplayAtlas: TextureAtlas? = null
    private var stage: Stage? = null

    init {
        assetManager = game.assetManager
    }

    override fun show() {
        viewport = FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT)
        stage = Stage(viewport, game.batch)

        // load assets
        assetManager!!.load(AssetDescriptors.UI_SKIN)
        assetManager.load(AssetDescriptors.GAMEPLAY)
        assetManager.finishLoading() // blocks until all assets are loaded
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY)
    }

    override fun resize(width: Int, height: Int) {
        viewport!!.update(width, height, true)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.GRAY)
        game.screen = MapScreen(game)
        stage!!.act(delta)
        stage!!.draw()
    }

    override fun hide() {
        dispose()
    }

    override fun dispose() {
        stage!!.dispose()
    }
}