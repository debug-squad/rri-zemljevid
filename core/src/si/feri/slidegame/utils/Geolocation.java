package si.feri.slidegame.utils;

public class Geolocation {
    public double lat;
    public double lng;

    public Geolocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Lat: " + lat + "\nLng: " + lng;
    }

    public Geolocation round() {
        return new Geolocation(
                Math.floor(lat * 1_000_000.0) / 1_000_000.0,
                Math.floor(lng * 1_000_000.0) / 1_000_000.0
        );
    }
}
