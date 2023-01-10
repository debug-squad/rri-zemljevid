package si.feri.slidegame.utils;

public class Geolocation {
    public double lat;
    public double lng;


    public Geolocation() {
        this(46.557314, 15.637771);
    }
    public Geolocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Lat: " + lat + "\nLng: " + lng;
    }
}
