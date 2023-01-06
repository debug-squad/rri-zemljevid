package si.feri.slidegame.utils;

public class Geolocation {
    public double lat;
    public double lng;
    public String name;
    public String description;
    public String eventcreator;
    public String latitude;
    public String longitude;
    public String date;
    public String time;

    public Geolocation(double lat, double lng, String name,
     String description,
     String eventcreator,
     String latitude,
     String longitude,
     String date,
     String time) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.description=description;
        this.eventcreator=eventcreator;
        this.latitude=latitude;
        this.longitude=longitude;
        this.date=date;
    }

    @Override
    public String toString() {
        return "Lat: " + lat + "\nLng: " + lng;
    }

    public Geolocation round() {
        return new Geolocation(
                Math.floor(lat * 1_000_000.0) / 1_000_000.0,
                Math.floor(lng * 1_000_000.0) / 1_000_000.0,
                "","","","","","",""
        );
    }
}
