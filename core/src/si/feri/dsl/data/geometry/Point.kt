package si.feri.dsl.data.geometry

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("Point")
class Point(val coordinates: List<Float>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = "${indent(indent)}point((${coordinates[0]},${coordinates[1]}));"
}