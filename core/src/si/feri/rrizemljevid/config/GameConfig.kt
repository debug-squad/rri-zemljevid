package si.feri.rrizemljevid.config

import si.feri.rrizemljevid.utils.MapRasterTiles

object GameConfig {
    //
    //
    //
    const val ZOOM = 15
    const val NUM_TILES = 3
    const val MAP_WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES // world units
    const val MAP_HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES // world units
    const val MARKER_HEIGHT = 40f
    const val MARKER_WIDTH = 40f

    //
    //
    //
    const val ASPECT_RATIO = MAP_HEIGHT / MAP_WIDTH.toFloat()
    const val HUD_WIDTH = 800f // pixels
    const val HUD_HEIGHT = HUD_WIDTH * ASPECT_RATIO // pixels
}