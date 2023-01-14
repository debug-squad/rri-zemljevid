package si.feri.rrizemljevid

import com.badlogic.gdx.tools.texturepacker.TexturePacker

const val DRAW_DEBUG_OUTLINE = false
const val RAW_ASSETS_PATH = "assets-raw"
const val ASSETS_PATH = "assets"
fun main(args: Array<String>) {
    val settings = TexturePacker.Settings()
    settings.debug = DRAW_DEBUG_OUTLINE
    TexturePacker.process(
        settings,
        RAW_ASSETS_PATH + "/gameplay",  // the directory containing individual images to be packed
        ASSETS_PATH + "/gameplay",  // the directory where the pack file will be written
        "gameplay" // the name of the pack file / atlas name
    )
    TexturePacker.process(
        settings,
        RAW_ASSETS_PATH + "/skin",
        ASSETS_PATH + "/ui",
        "uiskin"
    )
}