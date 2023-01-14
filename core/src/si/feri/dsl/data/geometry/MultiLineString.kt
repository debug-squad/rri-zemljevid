package si.feri.dsl.data.geometry

import si.feri.rrizemljevid.utils.Geolocation

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("MultiLineString")
class MultiLineString(val coordinates: List<List<List<Float>>>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = coordinates.joinToString("\n", transform = { line ->
        "${indent(indent)}polyLine(${
            line.joinToString(",", "[", "]", transform = { p -> "(${p[0]},${p[1]})" })
        });"
    })

    override fun extract(
        points: MutableList<Geolocation>,
        lines: MutableList<List<Geolocation>>,
        polygons: MutableList<List<Geolocation>>
    ) {
        for (linePoints in coordinates) {
            val line = mutableListOf<Geolocation>()
            for (point in linePoints) {
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
}
