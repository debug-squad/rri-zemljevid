package si.feri.dsl.data.geometry

import si.feri.rrizemljevid.utils.Geolocation

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("Point")
class Point(val coordinates: List<Float>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = "${indent(indent)}point((${coordinates[0]},${coordinates[1]}));"
    override fun extract(
        points: MutableList<Geolocation>,
        lines: MutableList<List<Geolocation>>,
        polygons: MutableList<List<Geolocation>>
    ) {
        points.add(
            Geolocation(
                lng = coordinates[0].toDouble(),
                lat = coordinates[1].toDouble()
            )
        )
    }
}