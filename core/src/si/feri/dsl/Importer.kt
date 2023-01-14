package si.feri.dsl

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.modules.SerializersModule
import si.feri.dsl.data.features.Feature
import si.feri.dsl.data.features.FeatureCollection
import si.feri.dsl.data.features.IFeature
import si.feri.dsl.data.geometry.*
import java.io.File

fun main(args: Array<String>) {
    val data = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
        serializersModule = SerializersModule {
            polymorphic(IFeature::class, Feature::class, Feature.serializer())
            polymorphic(IFeature::class, FeatureCollection::class, FeatureCollection.serializer())

            polymorphic(IGeometryObject::class, GeometryCollection::class, GeometryCollection.serializer())
            polymorphic(IGeometryObject::class, LineString::class, LineString.serializer())
            polymorphic(IGeometryObject::class, MultiLineString::class, MultiLineString.serializer())
            polymorphic(IGeometryObject::class, MultiPoint::class, MultiPoint.serializer())
            polymorphic(IGeometryObject::class, Point::class, Point.serializer())
            polymorphic(IGeometryObject::class, Polygon::class, Polygon.serializer())
        }
    }.decodeFromStream<IFeature?>(File(args[0]).inputStream())
    println(data?.toDSL())
}
