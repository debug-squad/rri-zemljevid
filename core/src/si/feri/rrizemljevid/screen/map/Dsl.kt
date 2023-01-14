package si.feri.rrizemljevid.screen.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.scenes.scene2d.Actor
import si.feri.dsl.Parser
import si.feri.dsl.lexer.EvalAutomaton
import si.feri.dsl.lexer.Scanner
import si.feri.rrizemljevid.utils.Geolocation
import si.feri.rrizemljevid.utils.Map
import si.feri.rrizemljevid.utils.PixelPosition
import java.io.ByteArrayInputStream


class Dsl(val sr: ShapeRenderer, val map: Map, sb: SpriteBatch) : Actor() {
    val points: List<PixelPosition>
    val lines: List<FloatArray>
    val polygons: List<FloatArray>
    val polySprites: List<Polygon>

    init {

        val text = Gdx.files.internal("mydsl.dsl").readBytes()
        val scanner = Scanner(EvalAutomaton, ByteArrayInputStream(text))
        val parser = Parser(scanner)
        if (!parser.parse()) throw RuntimeException("Invalid DSL file")

        val pointsGeo = mutableListOf<Geolocation>()
        val linesGeo = mutableListOf<List<Geolocation>>()
        val polygonsGeo = mutableListOf<List<Geolocation>>()
        parser.currentFeature!!.extract(points = pointsGeo, lines = linesGeo, polygons = polygonsGeo)

        val points = pointsGeo.map { map.getPixelPosition(it)!! }
        val lines = linesGeo.map { it.map { geo -> map.getPixelPosition(geo)!! } }
        val polygons = polygonsGeo.map { it.map { geo -> map.getPixelPosition(geo)!! } }

        this.points = points + listOf(PixelPosition(100, 100))
        this.lines = lines.map { line ->
            line.fold(mutableListOf<Float>()) { acc, pixelPosition ->
                acc.add(pixelPosition.x.toFloat())
                acc.add(pixelPosition.y.toFloat())
                acc
            }.toFloatArray()
        }
        this.polygons = polygons.map { line ->
            line.fold(mutableListOf<Float>()) { acc, pixelPosition ->
                acc.add(pixelPosition.x.toFloat())
                acc.add(pixelPosition.y.toFloat())
                acc
            }.toFloatArray()
        }

        this.polySprites = this.polygons.map { Polygon(it) }

        for (p in this.points) println(p)
        sr.setAutoShapeType(true)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha);
        batch.end()
        sr.begin(ShapeRenderer.ShapeType.Line)

        sr.projectionMatrix = stage.camera.combined;

        sr.color = Color.BLUE;
        for (poly in polySprites) {
            sr.polyline(poly.transformedVertices)
        }

        sr.color = Color.BLUE;
        for (line in lines) {
            sr.polyline(line)
        }

        sr.set(ShapeRenderer.ShapeType.Filled)

        sr.color = Color.RED;
        for (point in points) {
            sr.circle(point.x.toFloat(), point.y.toFloat(), 5f)
        }

        sr.end()
        batch.begin()
    }
}