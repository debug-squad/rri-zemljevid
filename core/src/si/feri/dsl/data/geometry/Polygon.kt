package si.feri.dsl.data.geometry

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("Polygon")
class Polygon(val coordinates: List<List<List<Float>>>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = "${indent(indent)}polygon(${
        coordinates.joinToString(",", "[", "]", transform = { polygon ->
            polygon.joinToString(",", "[", "]", transform = { p -> "(${p[0]},${p[1]})" })
        })
    });"
}