package si.feri.dsl.data

interface IToDSL {
    fun toDSL(indent: Int = 0): String
    fun indent(indent: Int): String = " ".repeat(indent * 4)
}