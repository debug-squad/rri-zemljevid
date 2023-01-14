package si.feri.dsl.data.geometry

import si.feri.rrizemljevid.utils.Geolocation

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("MultiPoint")
class MultiPoint(val coordinates: List<List<Float>>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = coordinates.joinToString("\n", transform = { point ->
        "${indent(indent)}point((${point[0]},${point[1]}));"
    })

    override fun extract(
        points: MutableList<Geolocation>,
        lines: MutableList<List<Geolocation>>,
        polygons: MutableList<List<Geolocation>>
    ) {
        for (point in coordinates) {
            points.add(
                Geolocation(
                    lng = point[0].toDouble(),
                    lat = point[1].toDouble()
                )
            )
        }
    }
}
