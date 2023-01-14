package si.feri.rrizemljevid.screen.map

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import si.feri.rrizemljevid.config.GameConfig
import si.feri.rrizemljevid.database.Database
import si.feri.rrizemljevid.database.Event
import si.feri.rrizemljevid.utils.Geolocation
import si.feri.rrizemljevid.utils.Map
import si.feri.rrizemljevid.utils.PixelPosition
import java.io.IOException
import java.util.*

class Markers(mapScreen: MapScreen, val markerRegion: TextureRegion) : Group() {
    val map: Map?
    val sideMenue: SideMenue?
    private var refreshTime = 0f
    val markers = HashMap<UUID?, Marker>()

    init {
        map = mapScreen.map
        sideMenue = mapScreen.sideMenue
    }

    override fun act(delta: Float) {
        super.act(delta)

        //
        //
        //
        refreshTime -= delta
        if (refreshTime <= 0) {
            refreshTime = REFRESH_INTERVAL
            try {
                for (firEvent in Database.fetchEvents().values) {
                    var event: Event?
                    event = try {
                        firEvent.parse()
                    } catch (e: Exception) {
                        println(e)
                        println(firEvent)
                        Event()
                    }
                    val marker = markers[event!!.id]
                    if (marker != null) {
                        if (marker.updated || marker === sideMenue!!.marker) continue
                        marker.update(event)
                    } else {
                        val newMarker: Marker = Marker(event)
                        markers[event.id] = newMarker
                        addActor(newMarker)
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    //
    //
    //
    inner class Marker(event: Event?) : Image(
        markerRegion
    ) {
        var updated = false
        var event: Event? = null
        var pixelPosition: PixelPosition? = null

        init {
            width = GameConfig.MARKER_WIDTH
            height = GameConfig.MARKER_HEIGHT
            update(event)

            //
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    sideMenue!!.marker = this@Marker
                }
            })
        }

        fun update(event: Event?) {
            this.event = event
            recomputePosition()
        }

        fun recomputePosition() {
            pixelPosition = map!!.getPixelPosition(event!!.location)
            setPosition(pixelPosition!!.x.toFloat(), pixelPosition!!.y.toFloat())
        }

        fun setLocation(geo: Geolocation?) {
            event!!.location = geo!!
            pixelPosition = map!!.getPixelPosition(event!!.location)
            setPosition(pixelPosition!!.x - width / 2, pixelPosition!!.y.toFloat())
        }
    }

    companion object {
        //
        // Refresh
        //
        var REFRESH_INTERVAL = 10f
    }
}