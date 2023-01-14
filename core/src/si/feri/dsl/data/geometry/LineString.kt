package si.feri.dsl.data.geometry

import si.feri.rrizemljevid.utils.Geolocation

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("LineString")
class LineString(val coordinates: List<List<Float>>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = "${indent(indent)}polyLine(${
        coordinates.joinToString(
            ",",
            "[",
            "]",
            transform =
            { p -> "(${p[0]},${p[1]})" })
    });"

    override fun extract(
        points: MutableList<Geolocation>,
        lines: MutableList<List<Geolocation>>,
        polygons: MutableList<List<Geolocation>>
    ) {
        val line = mutableListOf<Geolocation>()
        for (point in coordinates) {
            line.add(
                Geolocation(
                    lng = point[0].toDouble(),
                    lat = point[1].toDouble()
                )
            )
        }
        lines.add(line)
    }
}