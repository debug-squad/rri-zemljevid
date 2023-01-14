package si.feri.rrizemljevid.utils

class Geolocation @JvmOverloads constructor(var lat: Double = 46.557314, var lng: Double = 15.637771) {
    override fun toString(): String {
        return "Lat: $lat\nLng: $lng"
    }
}