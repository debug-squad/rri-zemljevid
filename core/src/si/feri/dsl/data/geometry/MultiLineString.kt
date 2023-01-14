package si.feri.dsl.data.geometry

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("MultiLineString")
class MultiLineString(val coordinates: List<List<List<Float>>>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = coordinates.joinToString("\n", transform = { line ->
        "${indent(indent)}polyLine(${
            line.joinToString(",", "[", "]", transform = { p -> "(${p[0]},${p[1]})" })
        });"
    })
}
