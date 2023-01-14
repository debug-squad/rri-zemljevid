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
            name.setText(event.name)
            description.text = event.description
            latitude.text = event.latitude
            longitude.text = event.longitude
            eventCreator.text = event.eventCreator
            date.text = event.date
            time.text = event.time
        }
    var capture = false

    //
    //
    //
    var name: Label
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
        name = Label("geo.name", uskin)
        //name = new TextField("geo.name", uskin);
        description = TextField("geo.description", uskin)
        eventCreator = TextField("geo.eventcreator", uskin)
        latitude = TextField("geo.latitude", uskin)
        longitude = TextField("geo.longitude", uskin)
        date = TextField("geo.date", uskin)
        time = TextField("geo.time", uskin)
        val exitBtn = TextButton("Exit", uskin)
        exitBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                marker = null
            }
        })
        val width = GameConfig.HUD_WIDTH / 2f
        add(Label("", uskin)).width(width).row()
        add(name).fill().row()
        add(description).fill().row()
        add(eventCreator).fill().row()
        add(latitude).fill().row()
        add(longitude).fill().row()
        add(date).fill().row()
        add(time).fill().row()
        add(exitBtn).fill().row()
        //table.add(buttonEdit).center().padLeft(300);
        top().right()
        setWidth(width)
        pack()
        height = GameConfig.HUD_HEIGHT
        x = GameConfig.HUD_WIDTH - getWidth()
        val bgPixmap = Pixmap(1, 1, Pixmap.Format.RGB565)
        bgPixmap.setColor(Color.RED)
        bgPixmap.fill()
        val textureRegionDrawableBg = TextureRegionDrawable(TextureRegion(Texture(bgPixmap)))
        background = textureRegionDrawableBg

        //
        marker = null
    }

}