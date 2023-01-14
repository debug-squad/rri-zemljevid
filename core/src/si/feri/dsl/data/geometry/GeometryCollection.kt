package si.feri.dsl.data.geometry

import si.feri.rrizemljevid.utils.Geolocation

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("GeometryCollection")
class GeometryCollection(val geometries: List<IGeometryObject>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = geometries.joinToString("\n", transform = { it.toDSL(indent) })
    override fun extract(
        points: MutableList<Geolocation>,
        lines: MutableList<List<Geolocation>>,
        polygons: MutableList<List<Geolocation>>
    ) {
        for (geo in geometries) {
            geo.extract(points = points, lines = lines, polygons = polygons)
        }
    }
}