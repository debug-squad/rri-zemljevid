package si.feri.dsl

import si.feri.dsl.data.Properties
import si.feri.dsl.data.features.Feature
import si.feri.dsl.data.features.FeatureCollection
import si.feri.dsl.data.features.IFeature
import si.feri.dsl.data.geometry.*
import si.feri.dsl.data.geometry.*
import si.feri.dsl.lexer.Scanner
import si.feri.dsl.lexer.Token
import si.feri.dsl.lexer.TokenType.*
import kotlin.math.*

class Parser(private val scanner: Scanner) {
    private var token = scanner.getToken()
    var variables = java.util.ArrayDeque<HashMap<String, Any>>()
    var stack = java.util.ArrayDeque<Any>()
    var returnValue: Any = Unit
    var skip = false
    var debug = false

    var ret = false
    var bre = false
    var con = false

    var forCount = 0
    var fnCall = 0

    //
    // Data
    //

    var featureNestingPrev = -1
    var featureNesting = 0
    var currentFeature: IFeature? = null
    var parentFeatures = mutableListOf<IFeature>()
    var childFeatures = mutableListOf<IFeature>()

    //
    // Geomertry
    //

    var points: MutableList<List<Float>>? = null
    var lines: MutableList<List<List<Float>>>? = null
    var polygons: MutableList<List<List<Float>>>? = null

    // var line: MutableList<List<Float>>? = null

    //
    //
    //

    companion object {
        fun error(message: String? = null): Boolean {
            if (message != null) println("Error: $message")
            return false
        }
    }

    //
    //
    //

    init {
        val functions = HashMap<String, Any>()
        functions["line"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 2) return@Function error("Invalid number of args (expected 2)")
                val p2 = p.stack.pop()
                val p1 = p.stack.pop()
                if (p1 !is Point || p2 !is Point) return@Function error("Invalid argument types (expected (Point, Point))")
                if (p.featureNesting <= 0) return@Function return@Function error("Must be run inside a infrastructure block")

                val lines = p.lines ?: mutableListOf()
                lines.add(listOf(listOf(p1.x, p1.y), (listOf(p2.x, p2.y))))
                p.lines = lines

                return@Function true
            }
        functions["fst"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 1) return@Function error("Invalid number of args (expected 1)")
                val p1 = p.stack.pop()
                if (p1 !is Point) return@Function error("Invalid argument types (expected (Point))")

                returnValue = p1.x
                return@Function true
            }
        functions["snd"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 1) return@Function error("Invalid number of args (expected 1)")
                val p1 = p.stack.pop()
                if (p1 !is Point) return@Function error("Invalid argument types (expected (Point))")

                returnValue = p1.y
                return@Function true
            }
        functions["range"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                when (argsC) {
                    1 -> {
                        val end = p.stack.pop()
                        if (end !is Float) return@Function error("Invalid argument types (expected (Float))")
                        returnValue = (0 until end.toInt()).map { it.toFloat() }
                        return@Function true
                    }
                    2 -> {
                        val end = p.stack.pop()
                        val start = p.stack.pop()
                        if (start !is Float || end !is Float) return@Function error("Invalid argument types (expected (Float, Float))")
                        returnValue = (start.toInt() until end.toInt()).map { it.toFloat() }
                        return@Function true
                    }
                    3 -> {
                        val step = p.stack.pop()
                        val end = p.stack.pop()
                        val start = p.stack.pop()
                        if (start !is Float || end !is Float || step !is Float) return@Function error("Invalid argument types (expected (Float, Float, Float))")
                        returnValue = (start.toInt() until end.toInt() step step.toInt()).map { it.toFloat() }
                        return@Function true
                    }
                    else -> return@Function error("Invalid number of args (expected 1, 2 or 3)")
                }
            }
        functions["box"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 2) return@Function error("Invalid number of args (expected 2)")
                val p2 = p.stack.pop()
                val p1 = p.stack.pop()
                if (p1 !is Point || p2 !is Point) return@Function error("Invalid argument types (expected (Point, Point))")
                if (p.featureNesting <= 0) return@Function error("Must be run inside a infrastructure block")

                var box = mutableListOf<List<Float>>()
                box.add(listOf(p1.x, p1.y))
                box.add(listOf(p2.x, p1.y))
                box.add(listOf(p2.x, p2.y))
                box.add(listOf(p1.x, p2.y))
                box.add(listOf(p1.x, p1.y))

                var polys = p.polygons ?: mutableListOf()
                polys.add(box.toList())
                p.polygons = polys

                return@Function true
            }
        functions["circle"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 2) return@Function error("Invalid number of args (expected 2)")
                val radius = p.stack.pop()
                val p1 = p.stack.pop()
                if (p1 !is Point || radius !is Float) return@Function error("Invalid argument types (expected (Point, Float))")
                if (p.featureNesting <= 0) return@Function error("Must be run inside a infrastructure block")

                var box = mutableListOf<List<Float>>()
                for (angle in 0..360 step 10) {
                    val rad = Math.toRadians(angle.toDouble())
                    box.add(listOf(cos(rad).toFloat() * radius + p1.x, sin(rad).toFloat() * radius + p1.y))
                }
                box.add(listOf(cos(0.0).toFloat() * radius + p1.x, sin(0.0).toFloat() * radius + p1.y))

                var polys = p.polygons ?: mutableListOf()
                polys.add(box.toList())
                p.polygons = polys

                return@Function true
            }
        functions["elip"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 3) return@Function error("Invalid number of args (expected 3)")
                val yr = p.stack.pop()
                val xr = p.stack.pop()
                val p1 = p.stack.pop()
                if (p1 !is Point || yr !is Float || xr !is Float) return@Function error("Invalid argument types (expected (Point, Float, Float))")
                if (p.featureNesting <= 0) return@Function error("Must be run inside a infrastructure block")

                var box = mutableListOf<List<Float>>()
                for (angle in 0..360 step 10) {
                    val rad = Math.toRadians(angle.toDouble())
                    box.add(listOf(cos(rad).toFloat() * xr + p1.x, sin(rad).toFloat() * yr + p1.y))
                }
                box.add(listOf(cos(0.0).toFloat() * xr + p1.x, sin(0.0).toFloat() * yr + p1.y))

                var polys = p.polygons ?: mutableListOf()
                polys.add(box.toList())
                p.polygons = polys

                return@Function true
            }
        functions["sin"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 1) return@Function error("Invalid number of args (expected 1)")
                val v = p.stack.pop()
                if (v !is Float) return@Function error("Invalid argument types (expected (Float))")

                returnValue = sin(v)
                return@Function true
            }
        functions["cos"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 1) return@Function error("Invalid number of args (expected 1)")
                val v = p.stack.pop()
                if (v !is Float) return@Function error("Invalid argument types (expected (Float))")

                returnValue = cos(v)
                return@Function true
            }
        functions["tan"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 1) return@Function error("Invalid number of args (expected 1)")
                val v = p.stack.pop()
                if (v !is Float) return@Function error("Invalid argument types (expected (Float))")

                returnValue = tan(v)
                return@Function true
            }
        functions["len"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 1) return@Function error("Invalid number of args (expected 1)")
                val v = p.stack.pop()
                if (v !is Collection<*>) return@Function error("Invalid argument types (expected (Collection))")

                returnValue = v.size
                return@Function true
            }
        functions["bend"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (p.featureNesting <= 0) return@Function error("Must be run inside a infrastructure block")

                when (argsC) {
                    3 -> {
                        val rAngle = p.stack.pop()
                        val p1 = p.stack.pop()
                        val p0 = p.stack.pop()
                        if (p0 !is Point || p1 !is Point || rAngle !is Float) return@Function error("Invalid argument types (expected (Point, Point, Float))")

                        fun CubicN(t: Float, a: Float, b: Float, c: Float, d: Float) =
                            a + (-a * 3 + t * (3 * a - a * t)) * t + (3 * b + t * (-6 * b + b * 3 * t)) * t + (c * 3 - c * 3 * t) * (t * t) + d * ((t * t) * t)

                        val relativeAngle = Math.toRadians(rAngle.toDouble())
                        val oppositeRelativeAngle = Math.PI - relativeAngle

                        val angle = atan2(p1.y - p0.y, p1.x - p0.x)
                        val constant = hypot(p0.x - p1.x, p0.y - p1.y)

                        // Kontrolne točke
                        val a1 = angle + relativeAngle
                        val c0x = (p0.x + cos(a1) * constant).toFloat()
                        val c0y = (p0.y + sin(a1) * constant).toFloat()
                        val a2 = angle + oppositeRelativeAngle
                        val c1x = (p1.x + cos(a2) * constant).toFloat()
                        val c1y = (p1.y + sin(a2) * constant).toFloat()

                        val line = mutableListOf<List<Float>>()
                        for (i in 0..50) {
                            val t = i.toFloat() / 50
                            var x = CubicN(t, p0.x, c1x, c0x, p1.x)
                            var y = CubicN(t, p0.y, c1y, c0y, p1.y)
                            line.add(listOf(x, y))
                        }
                        val lines = p.lines ?: mutableListOf()
                        lines.add(line)
                        p.lines = lines

                        return@Function true
                    }

                    4 -> {
                        val p3 = p.stack.pop()
                        val p2 = p.stack.pop()
                        val p1 = p.stack.pop()
                        val p0 = p.stack.pop()
                        if (p0 !is Point || p1 !is Point || p2 !is Point || p3 !is Point) return@Function error("Invalid argument types (expected (Point, Point, Point, Point))")

                        fun CubicN(t: Float, a: Float, b: Float, c: Float, d: Float) =
                            a + (-a * 3 + t * (3 * a - a * t)) * t + (3 * b + t * (-6 * b + b * 3 * t)) * t + (c * 3 - c * 3 * t) * (t * t) + d * ((t * t) * t)

                        val line = mutableListOf<List<Float>>()
                        fun getPt(n1: Float, n2: Float, t: Float): Float = n1 + ((n2 - n1) * t)
                        for (i in 0..50) {
                            val t = i.toFloat() / 50
                            var x = CubicN(t, p0.x, p2.x, p3.x, p1.x)
                            var y = CubicN(t, p0.y, p2.y, p3.y, p1.y)
                            line.add(listOf(x, y))
                        }
                        val lines = p.lines ?: mutableListOf()
                        lines.add(line)
                        p.lines = lines

                        return@Function true
                    }
                    else -> return@Function error("Invalid number of args (expected 3 or 4)")
                }
            }


        //
        // Primitive Geometry
        //

        functions["point"] =
            Function("#inbuilt: line", mutableSetOf("x", "y"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (p.featureNesting <= 0) return@Function return@Function error("Must be run inside a infrastructure block")

                when (argsC) {
                    1 -> {
                        val p1 = p.stack.pop()
                        if (p1 !is Point) return@Function error("Invalid argument types (expected (Point))")
                        val points = p.points ?: mutableListOf()
                        points.add(listOf(p1.x, p1.y))
                        p.points = points
                        return@Function true
                    }
                    2 -> {
                        val y = p.stack.pop()
                        val x = p.stack.pop()
                        if (x !is Float || y !is Float) return@Function error("Invalid argument types (expected (Float, Float))")
                        val points = p.points ?: mutableListOf()
                        points.add(listOf(x, y))
                        p.points = points
                        return@Function true
                    }
                    else -> {
                        return@Function error("Invalid number of args (expected 1 or 2)")
                    }
                }
            }
        functions["polyLine"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 1) return@Function error("Invalid number of args (expected 1)")
                val v = p.stack.pop()
                if (v !is Iterable<*> || !v.all { it is Point }) return@Function error("Invalid argument types (expected (Iterable<Point>))")
                if (p.featureNesting <= 0) return@Function error("Must be run inside a infrastructure block")

                val lines = p.lines ?: mutableListOf()
                lines.add(v.map {
                    listOf(
                        (it as Point).x,
                        it.y
                    )
                })
                p.lines = lines
                return@Function true
            }
        functions["polygon"] =
            Function("#inbuilt: line", mutableSetOf("p1", "p2"), 0, null) { p: Parser, f: Function, argsC: Int ->
                if (argsC != 1) return@Function error("Invalid number of args (expected 1)")
                val v = p.stack.pop()
                if (v !is Iterable<*> || !v.all { it is Iterable<*> && it.all { v -> v is Point } }) return@Function error(
                    "Invalid argument types (expected (Iterable<Iterable<Point>>))"
                )
                if (p.featureNesting <= 0) return@Function error("Must be run inside a infrastructure block")

                val polygon = p.polygons ?: mutableListOf()
                polygon.addAll(v.map {
                    (it as Iterable<*>).map { v ->
                        listOf(
                            (v as Point).x,
                            v.y
                        )
                    }
                })
                p.polygons = polygon
                return@Function true
            }
        variables.push(functions)
    }

    //
    //
    //

    fun parse(): Boolean {
        return STATEMENTS() && scanner.eof()
    }

    fun STATEMENTS(): Boolean {
        if (!skip) variables.push(HashMap<String, Any>())

        if (!STATEMENT()) return false
        while (token?.value == SEMI.value) {
            token = scanner.getToken()
            if (!STATEMENT()) return false
        }

        if (!skip) {
            val vars = variables.pop()
            if (debug) println("Vars: $vars")
        }
        return true
    }

    fun STATEMENT(): Boolean {
        when (token?.value) {
            COLON.value -> {
                token = scanner.getToken()
                if (token?.value != VARIABLE.value) return false
                val infrastructureType = token!!.lexeme
                token = scanner.getToken()
                val last = token
                var hasName = false
                if (!EXPRESSION()) {
                    if (token != last) return false
                } else {
                    hasName = true
                }

                // Get name
                var name = if (!skip && hasName) {
                    val tmp = stack.pop()
                    if (tmp !is String) return error("Block name isn't string")
                    tmp
                } else {
                    null
                }

                featureNestingPrev = featureNesting
                featureNesting++
                if (!LAMBDA_BODY()) return false
                featureNesting--

                if (!skip) {
                    if (featureNestingPrev != featureNesting) {
                        childFeatures = parentFeatures
                        parentFeatures = mutableListOf()
                    }

                    //
                    // Expression append
                    //

                    when (val returnValue = returnValue) {
                        is Unit -> {}
                        is Point -> {
                            val tmp = points ?: mutableListOf()
                            tmp.add(listOf(returnValue.x, returnValue.y))
                            points = tmp
                        }
                        is Iterable<*> -> {
                            if (returnValue.all { it is Point }) {
                                val tmp = lines ?: mutableListOf()
                                tmp.add(returnValue.map {
                                    listOf(
                                        (it as Point).x,
                                        it.y
                                    )
                                })
                                lines = tmp
                            } else if (returnValue.all { it is Iterable<*> && it.all { v -> v is Point } }) {
                                val tmp = polygons ?: mutableListOf()
                                tmp.addAll(returnValue.map {
                                    (it as Iterable<*>).map { v ->
                                        listOf(
                                            (v as Point).x,
                                            v.y
                                        )
                                    }
                                })
                                polygons = tmp
                            }
                        }
                        else -> return error("Block expression type is invalid")
                    }
                    returnValue = Unit

                    //
                    // Feature builder
                    //

                    if (points != null || lines != null || polygons != null) {
                        if (childFeatures.isNotEmpty()) return error("[$featureNesting:$infrastructureType:$name] Block cannot contain shape/s and sub blocks")

                        // Geometry
                        val geometries = listOfNotNull(
                            points?.let {
                                when (it.size) {
                                    1 -> Point(it[0])
                                    else -> MultiPoint(it)
                                }
                            },
                            lines?.let {
                                when (it.size) {
                                    1 -> LineString(it[0])
                                    else -> MultiLineString(it)
                                }
                            },
                            polygons?.let { Polygon(it) }
                        )
                        val geometry: IGeometryObject = when (geometries.size) {
                            1 -> geometries[0]
                            else -> GeometryCollection(geometries)
                        }

                        // Clear
                        points = null
                        lines = null
                        polygons = null

                        // Create feature
                        val feature = Feature(
                            geometry = geometry,
                            properties = Properties(infrastructureType, name)
                        )
                        currentFeature = feature
                        parentFeatures.add(currentFeature!!)
                    } else {
                        val features = childFeatures
                        childFeatures = mutableListOf()

                        // Create feature
                        val feature = FeatureCollection(
                            features = features,
                            properties = Properties(infrastructureType, name)
                        )
                        currentFeature = feature
                        parentFeatures.add(currentFeature!!)
                    }

                    //
                    // Rot node
                    //

                    if (featureNesting == 0 && parentFeatures.size > 1)
                        return error("There cant be more than one root block")
                }
                return true
            }
            FN.value -> {
                token = scanner.getToken()
                if (token?.value != VARIABLE.value) return false
                val fnName = token?.lexeme!!
                token = scanner.getToken()
                if (token?.value != LPAREN.value) return false
                token = scanner.getToken()

                var args = mutableSetOf<String>()
                if (token?.value == VARIABLE.value) {
                    args.add(token?.lexeme!!)
                    token = scanner.getToken()

                    while (token?.value == COMMA.value) {
                        token = scanner.getToken()
                        if (token?.value != VARIABLE.value) return false
                        if (!args.add(token?.lexeme!!)) return false
                        token = scanner.getToken()
                    }
                }
                if (token?.value != RPAREN.value) return false

                if (!skip) {
                    val pos = scanner.getPosition()
                    token = scanner.getToken()
                    variables.peek()[fnName] = Function("named $fnName", args, pos!!, token)
                }
                val skipSaved = skip
                skip = true
                if (!LAMBDA_BODY()) return false
                skip = skipSaved
                return true
            }
            LBR.value -> {
                token = scanner.getToken()
                if (!STATEMENTS()) return false
                if (token?.value != RBR.value) return false
                token = scanner.getToken()
                return true
            }
            IF.value -> {
                token = scanner.getToken()
                if (!EXPRESSION()) return false
                if (token?.value != LBR.value) return false
                token = scanner.getToken()

                val trueBranch = token
                val saved = skip
                skip = true
                if (!STATEMENTS()) return false
                skip = saved

                if (token?.value != RBR.value) return false
                token = scanner.getToken()

                var falseBranch: Token? = null
                if (token?.value == ELSE.value) {
                    token = scanner.getToken()
                    if (token?.value != LBR.value) return false
                    token = scanner.getToken()

                    falseBranch = token
                    val saved = skip
                    skip = true
                    if (!STATEMENTS()) return false
                    skip = saved

                    if (token?.value != RBR.value) return false
                    token = scanner.getToken()
                }

                if (!skip) {
                    val condition = stack.pop()
                    if (condition !is Boolean) return error("Condition isn't a boolean")
                    val branch = if (condition) {
                        trueBranch
                    } else {
                        falseBranch
                    }
                    if (branch != null) {
                        // Save initial
                        val t = token
                        val p = scanner.getPosition()!!

                        // Set
                        token = branch
                        scanner.setPosition(branch.start)

                        // Execute
                        if (!STATEMENTS()) return false

                        // Reset
                        scanner.setPosition(t?.start ?: p)
                        token = t
                    }
                }
                return true
            }
            FOR.value -> {
                token = scanner.getToken()
                if (token?.value != VARIABLE.value) return false
                val varName = token?.lexeme!!
                token = scanner.getToken()
                if (token?.value != IN.value) return false
                token = scanner.getToken()
                if (!EXPRESSION()) return false
                if (token?.value != LBR.value) return false
                token = scanner.getToken()

                // Skip and save start
                val startToken = token
                val saved = skip
                skip = true
                if (!STATEMENTS()) return false
                skip = saved

                if (!skip) {
                    val iterator = stack.pop()
                    if (iterator !is Iterable<*>) return error("For value isn't iterable")
                    variables.push(HashMap<String, Any>())
                    forCount++

                    for (item in iterator) {
                        skip = false
                        con = false
                        bre = false

                        variables.peek()[varName] = item as Any
                        token = startToken
                        scanner.setPosition(startToken!!.start)

                        if (!STATEMENTS()) return false

                        if (bre) break
                        if (con) continue
                    }
                    skip = false
                    con = false
                    bre = false
                    forCount--
                    variables.pop()
                }

                if (token?.value != RBR.value) return false
                token = scanner.getToken()
                return true
            }
            LET.value -> {
                token = scanner.getToken()
                if (token?.value != VARIABLE.value) return false
                val varName = token?.lexeme!!
                token = scanner.getToken()
                if (token?.value == ASSIGN.value) {
                    token = scanner.getToken()
                    if (!EXPRESSION()) return false
                    if (!skip) {
                        variables.peek()[varName] = stack.pop()
                    }
                    return true
                }
                if (!skip) {
                    variables.peek()[varName] = Unit
                }
                return true
            }
            VARIABLE.value -> {
                val varName = token?.lexeme!!
                token = scanner.getToken()
                when (token?.value) {
                    ASSIGN.value -> {
                        token = scanner.getToken()
                        if (!EXPRESSION()) return false
                        if (!skip) {
                            val vars = variables.find { it.containsKey(varName) }
                                ?: return error("Varible '$varName' isn't defined")
                            vars[varName] = stack.pop()
                        }
                        return true
                    }
                    LPAREN.value -> {
                        token = scanner.getToken()
                        var len = 0
                        val last = token
                        if (!EXPRESSION()) {
                            if (token != last) return false
                        } else {
                            len++
                            while (token?.value == COMMA.value) {
                                token = scanner.getToken()
                                if (!EXPRESSION()) return false
                                len++
                            }
                        }
                        if (token?.value != RPAREN.value) return false
                        token = scanner.getToken()

                        if (!skip) {
                            val vars = variables.find { it.containsKey(varName) }
                                ?: return error("Function '$varName' isn't defined")
                            val function = vars[varName] ?: return error("Function '$varName' isn't defined")
                            if (function !is Function) return error("'$varName' isn't a Function")
                            if (!executeFunction(function, len)) return false
                        }
                        return true
                    }
                    else -> return false
                }
            }
            RETURN.value -> {
                token = scanner.getToken()
                val last = token
                if (!EXPRESSION()) {
                    if (token != last) return false

                    if (!skip) {
                        if (fnCall <= 0) return false
                        returnValue = Unit
                        skip = true
                    }
                    return true
                }
                if (!skip) {
                    if (fnCall <= 0) return error("Cou can only return in a function")
                    returnValue = stack.pop()
                    skip = true
                }
                return true
            }
            CONTINUE.value -> {
                token = scanner.getToken()
                if (!skip) {
                    if (forCount <= 0) return error("You can only continue in a loop")
                    con = true
                    skip = true
                }
                return true
            }
            BREAK.value -> {
                token = scanner.getToken()
                if (!skip) {
                    if (forCount <= 0) return error("You can only break in a loop")
                    bre = true
                    skip = true
                }
                return true
            }
            LINE.value -> {
                token = scanner.getToken()
                if (!EXPRESSION()) return false

                while (token?.value == LINE.value) {
                    token = scanner.getToken()
                    if (!EXPRESSION()) return false

                    // Save last
                    val p2 = if (!skip) stack.peek() else null

                    var varName = "line"
                    var len = 2
                    if (token?.value == VARIABLE.value) {
                        varName = token!!.lexeme
                        token = scanner.getToken()
                        if (token?.value == LPAREN.value) {
                            token = scanner.getToken()
                            val last = token
                            if (!EXPRESSION()) {
                                if (token != last) return false
                            } else {
                                len++
                                while (token?.value == COMMA.value) {
                                    token = scanner.getToken()
                                    if (!EXPRESSION()) return false
                                }
                            }
                            if (token?.value != RPAREN.value) return false
                            token = scanner.getToken()
                        }
                    }

                    // Execute
                    if (!skip) {
                        val vars = variables.find { it.containsKey(varName) }
                            ?: return error("Function '$varName' isn't defined")
                        val function = vars[varName] ?: return error("Function '$varName' isn't defined")
                        if (function !is Function) return error("'$varName' isn't a Function")
                        if (!executeFunction(function, len)) return false
                        stack.push(p2!!)
                    }
                }

                // Cleanup
                if (!skip) {
                    stack.pop()
                }
                return true
            }
            else -> return true
        }
    }

    //
    //
    //

    fun EXPRESSION(): Boolean = EXPR_08()

    fun EXPR_08(): Boolean {
        if (!EXPR_07()) return false

        var save = skip
        while (token?.value == OR.value) {
            token = scanner.getToken()
            if (!skip) {
                val e = stack.peek()
                if (e !is Boolean) return error("You can only OR Booleans")
                if (e) skip = true
            }
            if (!EXPR_07()) return false
            if (!skip) {
                var b = stack.pop()
                var a = stack.pop()
                if (a !is Boolean || b !is Boolean) return error("You can only OR Booleans")
                stack.push(a || b)
            }
        }
        skip = save
        return true
    }

    fun EXPR_07(): Boolean {
        if (!EXPR_06()) return false

        var save = skip
        while (token?.value == AND.value) {
            token = scanner.getToken()
            if (!skip) {
                val e = stack.peek()
                if (e !is Boolean) return error("You can only AND Booleans")
                if (!e) skip = true
            }
            if (!EXPR_06()) return false
            if (!skip) {
                var b = stack.pop()
                var a = stack.pop()
                if (a !is Boolean || b !is Boolean) return error("You can only AND Booleans")
                stack.push(a && b)
            }
        }
        skip = save
        return true
    }

    fun EXPR_06(): Boolean {
        if (!EXPR_05()) return false
        when (token?.value) {
            EQ.value -> {
                token = scanner.getToken()
                if (!EXPR_05()) return false
                if (!skip) {
                    var b = stack.pop()
                    var a = stack.pop()
                    stack.push(a == b)
                }
                return true
            }
            NOTEQ.value -> {
                token = scanner.getToken()
                if (!EXPR_05()) return false
                if (!skip) {
                    var b = stack.pop()
                    var a = stack.pop()
                    stack.push(a != b)
                }
                return true
            }
            else -> return true
        }
    }

    fun EXPR_05(): Boolean {
        if (!EXPR_04()) return false
        when (token?.value) {
            MORE.value -> {
                token = scanner.getToken()
                if (!EXPR_04()) return false
                if (!skip) {
                    var b = stack.pop()
                    var a = stack.pop()
                    if (a !is Float || b !is Float) return error("You can only Compare Floats")
                    stack.push(a > b)
                }
                return true
            }
            MOREEQ.value -> {
                token = scanner.getToken()
                if (!EXPR_04()) return false
                if (!skip) {
                    var b = stack.pop()
                    var a = stack.pop()
                    if (a !is Float || b !is Float) return error("You can only Compare Floats")
                    stack.push(a >= b)
                }
                return true
            }
            LESS.value -> {
                token = scanner.getToken()
                if (!EXPR_04()) return false
                if (!skip) {
                    var b = stack.pop()
                    var a = stack.pop()
                    if (a !is Float || b !is Float) return error("You can only Compare Floats")
                    stack.push(a < b)
                }
                return true
            }
            LESSEQ.value -> {
                token = scanner.getToken()
                if (!EXPR_04()) return false
                if (!skip) {
                    var b = stack.pop()
                    var a = stack.pop()
                    if (a !is Float || b !is Float) return error("You can only Compare Floats")
                    stack.push(a <= b)
                }
                return true
            }
            else -> return true
        }
    }

    fun EXPR_04(): Boolean {
        if (!EXPR_03()) return false
        while (true) {
            when (token?.value) {
                PLUS.value -> {
                    token = scanner.getToken()
                    if (!EXPR_03()) return false
                    if (!skip) {
                        var b = stack.pop()
                        var a = stack.pop()
                        if (a is Float && b is Float) {
                            stack.push(a + b)
                        } else if (a is String) {
                            stack.push(a + b)
                        } else {
                            return error("You an only add Floats or concat strings")
                        }

                    }
                }
                MINUS.value -> {
                    token = scanner.getToken()
                    if (!EXPR_03()) return false
                    if (!skip) {
                        var b = stack.pop()
                        var a = stack.pop()
                        if (b !is Float || a !is Float) return error("You can only Substract Floats")
                        stack.push(a - b)
                    }
                }
                else -> return true
            }
        }
    }

    fun EXPR_03(): Boolean {
        if (!EXPR_02()) return false
        while (true) {
            when (token?.value) {
                TIMES.value -> {
                    token = scanner.getToken()
                    if (!EXPR_02()) return false
                    if (!skip) {
                        var b = stack.pop()
                        var a = stack.pop()
                        if (a !is Float || b !is Float) return error("You can only Multiply Floats")

                        stack.push(a * b)
                    }
                }
                DIVIDE.value -> {
                    token = scanner.getToken()
                    if (!EXPR_02()) return false
                    if (!skip) {
                        var b = stack.pop()
                        var a = stack.pop()
                        if (a !is Float || b !is Float) return error("You can only Divide Floats")
                        stack.push(a / b)
                    }
                }
                MOD.value -> {
                    token = scanner.getToken()
                    if (!EXPR_02()) return false
                    if (!skip) {
                        var b = stack.pop()
                        var a = stack.pop()
                        if (a !is Float || b !is Float) return error("You can only Modulo Floats")
                        stack.push(a % b)
                    }
                }
                else -> return true
            }
        }
    }

    fun EXPR_02(): Boolean {
        if (!EXPR_01()) return false
        var len = 0
        while (token?.value == POW.value) {
            len++
            token = scanner.getToken()
            if (!EXPR_01()) return false
        }
        if (!skip && len != 0) {
            var exp = 1f
            for (i in 1..len) {
                val v = stack.pop()
                if (v !is Float) return error("You can only Pow Floats")
                exp *= v
            }

            val v = stack.pop()
            if (v !is Float) return error("You can only Pow Floats")
            stack.push(v.pow(exp))
        }
        return true
    }

    fun EXPR_01(): Boolean {
        when (token?.value) {
            PLUS.value -> {
                token = scanner.getToken()
                if (!EXPR_00()) return false
                return true
            }
            MINUS.value -> {
                token = scanner.getToken()
                if (!EXPR_00()) return false
                if (!skip) {
                    val value = stack.pop()
                    if (value !is Float) return error("You can only Neg Floats")
                    stack.push(-value)
                }
                return true
            }
            NOT.value -> {
                token = scanner.getToken()
                if (!EXPR_00()) return false
                if (!skip) {
                    val value = stack.pop()
                    if (value !is Boolean) return error("You can only Neg Booleans")
                    stack.push(!value)
                }
                return true
            }
            else -> {
                if (!EXPR_00()) return false
                return true
            }
        }
    }

    fun EXPR_00(): Boolean {
        when (token?.value) {
            VARIABLE.value -> {
                val varName = token?.lexeme!!
                token = scanner.getToken()
                if (token?.value == LPAREN.value) {
                    token = scanner.getToken()
                    var len = 0

                    val last = token
                    if (!EXPRESSION()) {
                        if (token != last) return false
                    } else {
                        len++
                        while (token?.value == COMMA.value) {
                            token = scanner.getToken()
                            if (!EXPRESSION()) return false
                            len++
                        }
                    }
                    if (token?.value != RPAREN.value) return false
                    token = scanner.getToken()

                    if (!skip) {
                        val vars = variables.find { it.containsKey(varName) }
                            ?: return error("Function '$varName' isn't defined")
                        val function = vars[varName] ?: return error("Function '$varName' isn't defined")
                        if (function !is Function) return error("'$varName' isn't a Function")
                        if (!executeFunction(function, len)) return false
                        stack.push(returnValue)
                        returnValue = Unit
                    }
                    return true
                }
                if (!skip) {
                    val vars =
                        variables.find { it.containsKey(varName) } ?: return error("Variable '$varName' isn't defined")
                    stack.push(vars[varName] ?: return error("Variable '$varName' isn't defined"))
                }
                return true
            }
            LPAREN.value -> {
                token = scanner.getToken()
                if (!EXPRESSION()) return false
                if (token?.value == COMMA.value) {
                    token = scanner.getToken()
                    if (!EXPRESSION()) return false
                    if (token?.value != RPAREN.value) return false
                    token = scanner.getToken()

                    if (!skip) {
                        val y = stack.pop()
                        val x = stack.pop()
                        if (y !is Float || x !is Float) return error("You can only convert Flots into a Point")
                        stack.push(Point(x, y))
                    }
                    return true
                }
                if (token?.value != RPAREN.value) return false
                token = scanner.getToken()
                return true
            }
            NULL.value -> {
                if (!skip) stack.push(Unit)
                token = scanner.getToken()
                return true
            }
            FLOAT.value -> {
                if (!skip) stack.push(token?.lexeme?.toFloat()!!)
                token = scanner.getToken()
                return true
            }
            STR0.value -> {
                if (!skip) {
                    val value = token?.lexeme!!
                    stack.push(value.substring(3, value.length - 2))
                }
                token = scanner.getToken()
                return true
            }
            STR1.value -> {
                if (!skip) {
                    val value = token?.lexeme!!
                    stack.push(value.substring(1, value.length - 1))
                }
                token = scanner.getToken()
                return true
            }
            STR2.value -> {
                if (!skip) {
                    val value = token?.lexeme!!
                    stack.push(value.substring(1, value.length - 1))
                }
                token = scanner.getToken()
                return true
            }
            TRUE.value -> {
                if (!skip) stack.push(true)
                token = scanner.getToken()
                return true
            }
            FALSE.value -> {
                if (!skip) stack.push(false)
                token = scanner.getToken()
                return true
            }
            FN.value -> {
                token = scanner.getToken()
                if (token?.value != LPAREN.value) return false
                token = scanner.getToken()

                var args = mutableSetOf<String>()
                if (token?.value == VARIABLE.value) {
                    args.add(token?.lexeme!!)
                    token = scanner.getToken()
                    while (token?.value == COMMA.value) {
                        token = scanner.getToken()
                        if (token?.value != VARIABLE.value) return false
                        if (!args.add(token?.lexeme!!)) return false
                        token = scanner.getToken()
                    }
                }
                if (token?.value != RPAREN.value) return false

                if (!skip) {
                    val pos = scanner.getPosition()
                    token = scanner.getToken()
                    stack.push(Function("anonamus", args, pos!!, token))
                }

                val skipSaved = skip
                skip = true
                if (!LAMBDA_BODY()) return false
                skip = skipSaved

                return true
            }
            LSQBR.value -> {
                token = scanner.getToken()
                val last = token
                var len = 0
                if (!EXPRESSION()) {
                    if (token != last) return false
                } else {
                    len++
                    while (token?.value == COMMA.value) {
                        token = scanner.getToken()
                        if (!EXPRESSION()) return false
                        len++
                    }
                }
                if (token?.value != RSQBR.value) return false
                token = scanner.getToken()

                if (!skip) {
                    var data = java.util.ArrayDeque<Any>()
                    for (i in 1..len) data.push(stack.pop())
                    stack.push(data)
                }
                return true
            }
            else -> return false
        }
    }

    fun LAMBDA_BODY(): Boolean {
        when (token?.value) {
            LBR.value -> {
                token = scanner.getToken()
                if (!STATEMENTS()) return false
                if (token?.value != RBR.value) return false
                token = scanner.getToken()
                return true
            }
            ASSIGN.value -> {
                token = scanner.getToken()
                if (!EXPRESSION()) return false
                if (!skip) returnValue = stack.pop()
                return true
            }
            else -> return false
        }
    }

    //
    //
    //

    fun executeFunction(f: Function, argsC: Int): Boolean {
        // Pre execute state
        returnValue = Unit
        fnCall++

        val fC = forCount
        forCount = 0

        // Execute
        if (!run {
                // Run inbuilt
                if (f.f != null) return@run (f.f)(this, f, argsC)

                //
                // Run default
                //

                // Args
                if (argsC != f.args.size) return error("Invalid number of args (expected ${f.args.size})")
                val vars = HashMap<String, Any>()
                for (name in f.args.reversed()) vars[name] = stack.pop()
                if (debug) println("$f : $vars")

                // Push variable env
                variables.push(vars)

                // Save state
                val t = token
                val p = scanner.getPosition()!!

                // Set new state
                scanner.setPosition(f.position)
                token = f.token

                // Execute
                if (!LAMBDA_BODY()) return@run false

                // Reset state
                scanner.setPosition(t?.start ?: p)
                token = t

                // Pop variable env
                variables.pop()

                return@run true
            }) return false
        if (debug) println(" = $returnValue")

        // After execute state
        ret = false
        skip = false
        forCount = fC

        fnCall--
        return true
    }

    data class Point(val x: Float, val y: Float) {
        override fun toString() = "($x,$y)"
    }

    data class Function(
        val name: String,
        val args: MutableSet<String>,
        val position: Long,
        val token: Token?,
        val f: ((p: Parser, f: Function, argsC: Int) -> Boolean)? = null
    )
}