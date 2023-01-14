package si.feri.dsl.data.features

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import si.feri.dsl.data.Properties

@kotlinx.serialization.Serializable
@kotlinx.serialization.SerialName("FeatureCollection")
data class FeatureCollection(val features: List<IFeature>, val properties: Properties?) : IFeature() {
    override fun toDSL(indent: Int): String =
        "${indent(indent)}:${properties?.type ?: "unknown"}${properties?.name?.let { " " + Json.encodeToString(it) } ?: ""} {\n${
            features.joinToString(
                "\n",
                transform = {
                    it.toDSL(indent + 1)
                }
            ).let {
                if (it.isEmpty()) {
                    ""
                } else {
                    it + "\n"
                }
            }
        }${indent(indent)}};"
}