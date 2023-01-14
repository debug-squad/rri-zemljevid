package si.feri.dsl.lexer

import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

class Scanner(private val automaton: Automaton, private var inputStream: InputStream) {
    companion object {
        const val EOF_SYMBOL = -1
        const val ERROR_STATE = 0
        const val SKIP_VALUE = 0
        const val NEWLINE = '\n'.code
    }

    private val stream = object: BufferedInputStream(inputStream) {
        fun position(): Long = this.pos.toLong()
    }

    private var state = automaton.startState
    private var last: Int = stream.read()
    private var buffer = LinkedList<Byte>()
    private var row = 1
    private var column = 1





    private fun updatePosition(symbol: Int) {
        if (symbol == NEWLINE) {
            row += 1
            column = 1
        } else {
            column += 1
        }
    }

    private fun getValue(): Int {
        var symbol = last
        state = automaton.startState

        while (true) {
            updatePosition(symbol)

            val nextState = automaton.next(state, symbol)
            if (nextState == ERROR_STATE) {
                if (automaton.finalStates.contains(state)) {
                    last = symbol
                    return automaton.value(state)
                } else throw Error("Invalid pattern at ${row}:${column}")
            }
            state = nextState
            buffer.add(symbol.toByte())
            symbol = stream.read()
        }
    }

    fun eof(): Boolean =
        last == EOF_SYMBOL

    fun getToken(): Token? {
        if (eof()) return null

        val pos = stream.position() - 1
        val startRow = row
        val startColumn = column
        buffer.clear()


        val value = getValue()
        return if (value == SKIP_VALUE)
            getToken()
        else
            Token(value, String(buffer.toByteArray()), startRow, startColumn, stream.position(), pos)
    }

    //
    //
    //

    data class Position(val cursor: Long)

    fun getPosition(): Long? = stream.position()
    fun setPosition(position: Long): Token? {
        stream.position()
        last = stream.read()
        buffer.clear()
        return getToken()
    }
}