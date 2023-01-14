package si.feri.dsl.data.features

import com.badlogic.gdx.math.Vector2
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import si.feri.dsl.data.Properties
import si.feri.dsl.data.geometry.IGeometryObject
import si.feri.rrizemljevid.utils.Geolocation

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("Feature")
data class Feature(val geometry: IGeometryObject, val properties: Properties?) : IFeature() {
    override fun toDSL(indent: Int): String =
        "${indent(indent)}:${properties?.type ?: "unknown"}${properties?.name?.let { " " + Json.encodeToString(it) } ?: ""} {\n${
            geometry.toDSL(indent + 1).let {
                if (it.isEmpty()) {
                    ""
                } else {
                    it + "\n"
                }
            }
        }${indent(indent)}};"

    override fun extract(
        points: MutableList<Geolocation>,
        lines: MutableList<List<Geolocation>>,
        polygons: MutableList<List<Geolocation>>
    ) {
        geometry.extract(points = points, lines = lines, polygons = polygons)
    }
}