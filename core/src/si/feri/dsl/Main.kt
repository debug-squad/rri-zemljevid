package si.feri.dsl

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import si.feri.dsl.data.features.Feature
import si.feri.dsl.data.features.FeatureCollection
import si.feri.dsl.data.features.IFeature
import si.feri.dsl.data.geometry.*
import si.feri.dsl.lexer.EvalAutomaton
import si.feri.dsl.lexer.Scanner
import si.feri.dsl.lexer.TokenType
import java.io.File
import java.io.FileInputStream
import kotlin.system.exitProcess

fun printTokens(scanner: Scanner) {
    while (true) {
        val token = scanner.getToken() ?: break
        print("${TokenType.name(token.value)}(\"${token.lexeme}\") ")
    }
}

fun main(args: Array<String>) {
    val scanner = Scanner(EvalAutomaton, FileInputStream(File(args[0])))
    val parser = Parser(scanner)

    if (parser.parse()) {
        println(Json {
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
        }.encodeToString(parser.currentFeature))
    } else {
        println("rejected")
        printTokens(scanner)
        exitProcess(0)
    }
}