package si.feri.slidegame.screen.map;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import si.feri.slidegame.config.GameConfig;
import si.feri.slidegame.database.Database;
import si.feri.slidegame.database.Event;
import si.feri.slidegame.database.EventFirebase;
import si.feri.slidegame.utils.Geolocation;
import si.feri.slidegame.utils.Map;
import si.feri.slidegame.utils.PixelPosition;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Markers extends Group {
    public final Map map;
    public final SideMenue sideMenue;
    public final TextureRegion markerRegion;


    public Markers(MapScreen mapScreen, TextureRegion markerRegion) {
        this.map = mapScreen.map;
        this.markerRegion = markerRegion;
        this.sideMenue = mapScreen.sideMenue;
    }

    //
    // Refresh
    //

    public static float REFRESH_INTERVAL = 10f;
    private float refreshTime = 0;

    public final HashMap<UUID, Marker> markers = new HashMap<>();

    @Override
    public void act(float delta) {
        super.act(delta);

        //
        //
        //

        refreshTime -= delta;
        if (refreshTime <= 0) {
            refreshTime = REFRESH_INTERVAL;
            try {
                for (final EventFirebase firEvent : Database.fetchEvents().values()) {
                    Event event;
                    try {
                        event = firEvent.parse();
                    } catch (Exception e) {
                        System.out.println(e);
                        System.out.println(firEvent);
                        event = new Event();
                    }

                    Marker marker = markers.get(event.id);
                    if (marker != null) {
                        if(marker.updated || marker == sideMenue.marker) continue;
                        marker.update(event);
                    } else {
                        Marker newMarker = new Marker(event);
                        markers.put(event.id, newMarker);
                        addActor(newMarker);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    //
    //
    //

    public class Marker extends Image {
        public boolean updated = false;
        public Event event;
        public PixelPosition pixelPosition;

        public Marker(Event event) {
            super(markerRegion);
            setWidth(GameConfig.MARKER_WIDTH);
            setHeight(GameConfig.MARKER_HEIGHT);
            update(event);

            //
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    sideMenue.setMarker(Marker.this);
                }
            });
        }

        public void update(Event event) {
            this.event = event;
            recomputePosition();
        }

        public void recomputePosition() {
            pixelPosition = map.getPixelPosition(event.location);
            setPosition(pixelPosition.x, pixelPosition.y);
        }


        public void setLocation(Geolocation geo) {
            event.location = geo;
            pixelPosition = map.getPixelPosition(event.location);
            setPosition(pixelPosition.x - getWidth()/2, pixelPosition.y);
        }
    }
}
