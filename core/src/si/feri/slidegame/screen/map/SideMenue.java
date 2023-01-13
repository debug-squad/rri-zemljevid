package si.feri.slidegame.screen.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import si.feri.slidegame.config.GameConfig;
import si.feri.slidegame.database.EventFirebase;

public class SideMenue extends Table {
    //
    // Data
    //
    MapScreen screen;
    Markers.Marker marker = null;

    public boolean capture = false;


    //
    //
    //

    Label name;
    TextField description;
    TextField eventCreator;
    TextField latitude;
    TextField longitude;
    TextField date;
    TextField time;

    //
    //
    //


    public SideMenue(Skin uskin, final MapScreen screen) {
        super(uskin);
        this.screen = screen;
        //table.defaults().pad(20);

        name = new Label("geo.name", uskin);
        //name = new TextField("geo.name", uskin);
        description = new TextField("geo.description", uskin);
        eventCreator = new TextField("geo.eventcreator", uskin);
        latitude = new TextField("geo.latitude", uskin);
        longitude = new TextField("geo.longitude", uskin);
        date = new TextField("geo.date", uskin);
        time = new TextField("geo.time", uskin);



        TextButton exitBtn = new TextButton("Exit", uskin);
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setMarker(null);
            }
        });

        float width = GameConfig.HUD_WIDTH/2f;

        add(new Label("", uskin)).width(width).row();
        add(name).fill().row();
        add(description).fill().row();
        add(eventCreator).fill().row();
        add(latitude).fill().row();
        add(longitude).fill().row();
        add(date).fill().row();
        add(time).fill().row();
        add(exitBtn).fill().row();
        //table.add(buttonEdit).center().padLeft(300);

        top().right();
        setWidth(width);
        pack();
        setHeight(GameConfig.HUD_HEIGHT);
        setX(GameConfig.HUD_WIDTH - getWidth());


        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        bgPixmap.setColor(Color.RED); bgPixmap.fill();
        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
        setBackground(textureRegionDrawableBg);

        //
        setMarker(marker);
    }

    public void setMarker(Markers.Marker marker) {
        this.marker = marker;
        if(marker == null) {
            setVisible(false);
            return;
        }

        setVisible(true);
        EventFirebase event = new EventFirebase(marker.event);
        name.setText(event.name);
        description.setText(event.description);
        latitude.setText(event.latitude);
        longitude.setText(event.longitude);
        eventCreator.setText(event.eventCreator);
        date.setText(event.date);
        time.setText(event.time);
    }
}
