package si.feri.rrizemljevid.screen.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.Viewport
import si.feri.rrizemljevid.MyGdxGame
import si.feri.rrizemljevid.assets.AssetDescriptors
import si.feri.rrizemljevid.assets.RegionNames
import si.feri.rrizemljevid.config.GameConfig
import si.feri.rrizemljevid.utils.Map

class MapScreen(val game: MyGdxGame) : ScreenAdapter() {
    val assetManager: AssetManager?
    var viewport: Viewport? = null
    var hudViewport: Viewport? = null
    var stage: Stage? = null
    var hudStage: Stage? = null
    var mapGestureListener: MapGestureListener? = null
    var deploy: Boolean? = null

    //
    //
    //
    var shapeRenderer: ShapeRenderer? = null
    var touchPosition: Vector3? = null
    var sideMenue: SideMenue? = null
    var map: Map? = null
    var markers: Markers? = null
    var dsl: Dsl? = null

    //
    //
    //
    private var batch: SpriteBatch? = null
    private var uskin: Skin? = null
    private var gameplayAtlas: TextureAtlas? = null
    var pos: Label? = null

    //
    //
    //
    init {
        assetManager = game.assetManager
    }

    override fun show() {
        viewport = FillViewport(GameConfig.MAP_WIDTH.toFloat(), GameConfig.MAP_HEIGHT.toFloat())
        hudViewport = ExtendViewport(0f, GameConfig.HUD_HEIGHT)
        stage = Stage(viewport, game.batch)
        hudStage = Stage(hudViewport, game.batch)

        //stage.setDebugAll(true);
        //hudStage.setDebugAll(true);
        batch = SpriteBatch()

        //
        //
        //
        map = Map()
        stage!!.addActor(map)
        uskin = assetManager!!.get(AssetDescriptors.UI_SKIN)
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY)
        pos = Label("A, B, C", uskin)
        pos!!.width = GameConfig.HUD_WIDTH / 3.2f
        pos!!.height = pos!!.height * 2f
        pos!!.setPosition(5f, 5f)
        val labelColor = Pixmap(1, 1, Pixmap.Format.RGB888)
        labelColor.setColor(Color.BLACK)
        labelColor.fill()
        pos!!.style.background = Image(Texture(labelColor)).drawable
        hudStage!!.addActor(pos)

        //
        //
        //
        shapeRenderer = ShapeRenderer()
        touchPosition = Vector3()
        deploy = true
        mapGestureListener = MapGestureListener(stage!!.camera as OrthographicCamera, touchPosition!!, this)
        Gdx.input.inputProcessor = InputMultiplexer(
            stage,
            GestureDetector(mapGestureListener),
            hudStage
        )

        //
        sideMenue = SideMenue(uskin, this)
        hudStage!!.addActor(sideMenue)

        dsl = Dsl(ShapeRenderer(), map!!, SpriteBatch())
        stage!!.addActor(dsl)

        //
        markers = Markers(this, gameplayAtlas!!.findRegion(RegionNames.MARKER))
        stage!!.addActor(markers)
    }

    override fun resize(width: Int, height: Int) {
        viewport!!.update(width, height, true)
        hudViewport!!.update(width, height, true)
        sideMenue!!.x = hudViewport!!.worldWidth - sideMenue!!.width
        println("Worldd height is " + hudViewport!!.worldHeight + "Screen height is " + hudViewport!!.screenHeight)
        println("Worldd width is " + hudViewport!!.worldWidth + "Screen width is " + hudViewport!!.screenWidth)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.GRAY)

        //
        //
        //
        mapGestureListener!!.handleInput(delta)
        stage!!.act(delta)
        hudStage!!.act(delta)

        // Set debug position
        val loc = Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        val geo = map!!.getGeolocation(loc)
        pos!!.setText(geo.toString() + "")
        stage!!.viewport.apply()
        stage!!.draw()
        hudStage!!.viewport.apply()
        hudStage!!.draw()
    }

    override fun hide() {
        dispose()
    }

    override fun dispose() {
        shapeRenderer!!.dispose()
        stage!!.dispose()
        hudStage!!.dispose()
    }
}