package si.feri.dsl.data.geometry

import si.feri.rrizemljevid.utils.Geolocation

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("Polygon")
class Polygon(val coordinates: List<List<List<Float>>>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = "${indent(indent)}polygon(${
        coordinates.joinToString(",", "[", "]", transform = { polygon ->
            polygon.joinToString(",", "[", "]", transform = { p -> "(${p[0]},${p[1]})" })
        })
    });"

    override fun extract(
        points: MutableList<Geolocation>,
        lines: MutableList<List<Geolocation>>,
        polygons: MutableList<List<Geolocation>>
    ) {
        for (poly in coordinates) {
            val line = mutableListOf<Geolocation>()
            for (point in poly) {
                line.add(
                    Geolocation(
                        lng = point[0].toDouble(),
                        lat = point[1].toDouble()
                    )
                )
            }
            polygons.add(line)
        }
    }
}