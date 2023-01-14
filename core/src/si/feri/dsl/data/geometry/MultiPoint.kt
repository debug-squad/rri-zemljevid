package si.feri.dsl.data.geometry

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("MultiPoint")
class MultiPoint(val coordinates: List<List<Float>>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = coordinates.joinToString("\n", transform = { point ->
        "${indent(indent)}point((${point[0]},${point[1]}));"
    })
}
