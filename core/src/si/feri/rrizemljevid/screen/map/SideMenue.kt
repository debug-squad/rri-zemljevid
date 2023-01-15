package si.feri.rrizemljevid.screen.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import si.feri.rrizemljevid.config.GameConfig
import si.feri.rrizemljevid.database.EventFirebase

class SideMenue(
    uskin: Skin?, //
    // Data
    //
    var screen: MapScreen
) : Table(uskin) {
    var marker: Markers.Marker? = null
        set(marker) {
            field = marker
            if (marker == null) {
                isVisible = false
                return
            }
            isVisible = true
            val event = EventFirebase(marker.event)
            name.setText("Name: " + event.name)
            description.text = "Description: " +event.description
            latitude.text = "Latitude: " +event.latitude
            longitude.text = "Longitude :" +event.longitude
            eventCreator.text = "Event Creator: " + event.eventCreator
            date.text = "Event date: " +event.date
            time.text = "Event time: " +event.time
        }
    var capture = false

    //
    //
    //
    var name: TextField
    var description: TextField
    var eventCreator: TextField
    var latitude: TextField
    var longitude: TextField
    var date: TextField
    var time: TextField

    //
    //
    //
    init {
        //table.defaults().pad(20);
        name = TextField("geo.name", uskin)
        //name = new TextField("geo.name", uskin);
        description = TextField("geo.description", uskin)
        eventCreator = TextField("geo.eventcreator", uskin)
        latitude = TextField("geo.latitude", uskin)
        longitude = TextField("geo.longitude", uskin)
        date = TextField("geo.date", uskin)
        time = TextField("geo.time", uskin)
        val exitBtn = TextButton("Exit", uskin,"small")
        exitBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                marker = null
            }
        })
        exitBtn.setColor(Color.BLACK)
        val width = GameConfig.HUD_WIDTH / 2f
        add(name).fill().width(width).padTop(20f).padBottom(20f).row()
        add(description).fill().padBottom(20f).row()
        add(eventCreator).fill().padBottom(20f).row()
        add(latitude).fill().padBottom(20f).row()
        add(longitude).fill().padBottom(20f).row()
        add(date).fill().padBottom(20f).row()
        add(time).fill().padBottom(20f).row()
        add(exitBtn).row()
        //table.add(buttonEdit).center().padLeft(300);
        top().right()
        setWidth(width)
        pack()
        height = GameConfig.HUD_HEIGHT
        x = GameConfig.HUD_WIDTH - getWidth()
        val bgPixmap = Pixmap(1, 1, Pixmap.Format.RGB565)
        bgPixmap.setColor(Color.DARK_GRAY)
        bgPixmap.fill()
        val textureRegionDrawableBg = TextureRegionDrawable(TextureRegion(Texture(bgPixmap)))
        background = textureRegionDrawableBg

        //
        marker = null
    }

}