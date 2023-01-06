package si.feri.slidegame.screen.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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

        add(name).width(GameConfig.HUD_WIDTH/3).row();
        add(description).fill().row();
        add(eventcreator).fill().row();
        add(latitude).fill().row();
        add(longitude).fill().row();
        add(date).fill().row();
        add(time).fill().row();
        //table.add(buttonEdit).center().padLeft(300);

        top().right();
        setWidth(GameConfig.HUD_WIDTH/3);
        pack();
        setHeight(GameConfig.HUD_HEIGHT);
        setX(GameConfig.HUD_WIDTH - getWidth());


        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        bgPixmap.setColor(Color.RED); bgPixmap.fill();
        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
        setBackground(textureRegionDrawableBg);

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
