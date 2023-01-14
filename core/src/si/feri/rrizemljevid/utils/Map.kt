package si.feri.rrizemljevid.utils

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import si.feri.rrizemljevid.config.GameConfig
import java.io.IOException

class Map : Actor() {
    private val CENTER_GEOLOCATION = Geolocation(46.557314, 15.637771)

    //
    //
    //
    private val tiledMap: TiledMap
    private val tiledMapRenderer: TiledMapRenderer
    private var mapTiles: Array<Texture?>? = null
    var beginTile: ZoomXY? = null // top left tile

    //
    //
    //
    init {
        //
        // Download tiles
        //
        try {
            //in most cases, geolocation won't be in the center of the tile because tile borders are predetermined (geolocation can be at the corner of a tile)
            val centerTile =
                MapRasterTiles.getTileNumber(CENTER_GEOLOCATION.lat, CENTER_GEOLOCATION.lng, GameConfig.ZOOM)
            mapTiles = MapRasterTiles.getRasterTileZone(centerTile, GameConfig.NUM_TILES)
            //you need the beginning tile (tile on the top left corner) to convert geolocation to a location in pixels.
            beginTile = ZoomXY(
                GameConfig.ZOOM,
                centerTile!!.x - (GameConfig.NUM_TILES - 1) / 2,
                centerTile.y - (GameConfig.NUM_TILES - 1) / 2
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //
        // Gen Map
        //
        tiledMap = TiledMap()
        val layers = tiledMap.layers
        val layer = TiledMapTileLayer(
            GameConfig.NUM_TILES,
            GameConfig.NUM_TILES,
            MapRasterTiles.TILE_SIZE,
            MapRasterTiles.TILE_SIZE
        )
        var index = 0
        for (j in GameConfig.NUM_TILES - 1 downTo 0) {
            for (i in 0 until GameConfig.NUM_TILES) {
                val cell = TiledMapTileLayer.Cell()
                cell.tile = StaticTiledMapTile(
                    TextureRegion(
                        mapTiles!![index],
                        MapRasterTiles.TILE_SIZE,
                        MapRasterTiles.TILE_SIZE
                    )
                )
                layer.setCell(i, j, cell)
                index++
            }
        }
        layers.add(layer)
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
        width = (GameConfig.NUM_TILES * MapRasterTiles.TILE_SIZE).toFloat()
        height = (GameConfig.NUM_TILES * MapRasterTiles.TILE_SIZE).toFloat()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        tiledMapRenderer.setView(stage.camera as OrthographicCamera)
        tiledMapRenderer.render()
    }

    override fun drawDebugBounds(shapes: ShapeRenderer) {
        super.drawDebugBounds(shapes)
        if (!debug) return
        for (x in 0 until GameConfig.NUM_TILES) {
            for (y in 0 until GameConfig.NUM_TILES) {
                val px = getX() + MapRasterTiles.TILE_SIZE * x
                val py = getX() + MapRasterTiles.TILE_SIZE * y
                shapes.rect(
                    px,
                    py,
                    originX,
                    originY,
                    MapRasterTiles.TILE_SIZE.toFloat(),
                    MapRasterTiles.TILE_SIZE.toFloat(),
                    scaleX,
                    scaleY,
                    rotation
                )
            }
        }
    }

    //
    //
    //
    fun getPixelPosition(loc: Geolocation?): PixelPosition? {
        return MapRasterTiles.getPixelPosition(
            loc!!.lat,
            loc.lng,
            MapRasterTiles.TILE_SIZE,
            GameConfig.ZOOM,
            beginTile!!.x,
            beginTile!!.y,
            GameConfig.MAP_HEIGHT
        )
    }

    fun getGeolocation(loc: Vector2?): Geolocation? {
        val pos = stage.viewport.unproject(loc)
        return MapRasterTiles.getGeolocation(
            pos.x.toInt(),
            GameConfig.MAP_HEIGHT - pos.y.toInt(),
            MapRasterTiles.TILE_SIZE,
            GameConfig.ZOOM,
            beginTile!!.x,
            beginTile!!.y,
            GameConfig.MAP_HEIGHT
        )
    }
}