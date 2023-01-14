package si.feri.dsl.data.geometry

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

}