package si.feri.slidegame.screen.map;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import si.feri.slidegame.config.GameConfig;
import si.feri.slidegame.utils.Geolocation;

public class SideMenue extends Table {
    //
    // Data
    //
    Geolocation geo;


    //
    //
    //

    TextField name;
    TextField description;
    TextField eventcreator;
    TextField latitude;
    TextField longitude;
    TextField date;
    TextField time;

    //
    //
    //


    public SideMenue(Skin uskin) {
        super(uskin);
        //table.defaults().pad(20);

        name = new TextField("geo.name", uskin);
        description = new TextField("geo.description", uskin);
        eventcreator = new TextField("geo.eventcreator", uskin);
        latitude = new TextField("geo.latitude", uskin);
        longitude = new TextField("geo.longitude", uskin);
        date = new TextField("geo.date", uskin);
        time = new TextField("geo.time", uskin);

        TextButton buttonEdit = new TextButton("Edit", uskin);
        buttonEdit.setTransform(true);
        buttonEdit.setScale(0.3f);
        buttonEdit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        //Gdx.input.setInputProcessor(hudStage);

        add(name).row();
        add(description).row();
        add(eventcreator).row();
        add(latitude).row();
        add(longitude).row();
        add(date).row();
        add(time).row();
        //table.add(buttonEdit).center().padLeft(300);

        top().right();
        setWidth(GameConfig.WIDTH/3);
        pack();
        setHeight(GameConfig.HEIGHT);
        setDebug(true);
        setX(GameConfig.HUD_WIDTH - getWidth());

        //
        setGeo(null);
    }


    public void setGeo(Geolocation geo) {
        this.geo = geo;
        if(geo == null) {
            setVisible(false);
            return;
        }

        setVisible(true);
        name.setText(geo.name);
        description.setText(geo.description);
        eventcreator.setText(geo.eventcreator);
        latitude.setText(geo.latitude);
        longitude.setText(geo.longitude);
        date.setText(geo.date);
        time.setText(geo.time);
    }
}
