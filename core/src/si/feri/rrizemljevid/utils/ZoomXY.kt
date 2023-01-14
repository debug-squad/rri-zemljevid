package si.feri.rrizemljevid.utils

class ZoomXY(var zoom: Int, var x: Int, var y: Int) {
    override fun toString(): String {
        return "$zoom/$x/$y"
    }
}