package si.feri.dsl.lexer

data class Token(
    val value: Int,
    val lexeme: String,
    val startRow: Int,
    val startColumn: Int,
    val end: Long,
    val start: Long
)