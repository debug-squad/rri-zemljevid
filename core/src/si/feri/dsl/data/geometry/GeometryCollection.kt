package si.feri.dsl.data.geometry

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("GeometryCollection")
class GeometryCollection(val geometries: List<IGeometryObject>) : IGeometryObject() {
    override fun toDSL(indent: Int): String = geometries.joinToString("\n", transform = { it.toDSL(indent) })
}