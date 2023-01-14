package si.feri.dsl.lexer

import kotlin.math.max

object EvalAutomaton : Automaton {
    override var states = mutableSetOf(1)
    override val alphabet = 0..255
    override val startState = 1
    override var finalStates = mutableSetOf(2)

    private var numberOfSymbols = alphabet.maxOrNull()!! + 1
    private var transitions: ArrayList<IntArray> = ArrayList()
    private var values: ArrayList<Int> = ArrayList()


    private fun setTransition(from: Int, symbol: Char, to: Int) {
        if (from >= 1) states.add(from)
        if (to >= 1) states.add(to)
        for (i in transitions.size - 1..max(from, to)) transitions.add(IntArray(numberOfSymbols))
        transitions[from][symbol.code] = to
    }

    private fun setValue(state: Int, terminal: TokenType) {
        if (state >= 2) finalStates.add(state)
        for (i in values.size - 1..state) values.add(0)
        values[state] = terminal.value
    }

    override fun next(state: Int, symbol: Int): Int =
        if (symbol == Scanner.EOF_SYMBOL) Scanner.ERROR_STATE
        else {
            assert(states.contains(state))
            assert(alphabet.contains(symbol))
            transitions[state][symbol]
        }

    override fun value(state: Int): Int {
        assert(states.contains(state))
        return values[state]
    }

    private fun setVarTransition_(from: Int, to: Int) {
        for (c in 'A'..'Z') setTransition(from, c, to)
        for (c in 'a'..'z') setTransition(from, c, to)
        setTransition(from, '_', to)
    }

    private fun setVarTransition(from: Int, to: Int) {
        for (c in 'A'..'Z') setTransition(from, c, to)
        for (c in 'a'..'z') setTransition(from, c, to)
        for (c in '0'..'9') setTransition(from, c, to)
        setTransition(from, '_', to)
    }

    init {
        //float [0-9]+(.[0-9]+)?
        for (c in '0'..'9') setTransition(1, c, 2)
        for (c in '0'..'9') setTransition(2, c, 2)
        setTransition(2, '.', 3)
        for (c in '0'..'9') setTransition(3, c, 4)
        for (c in '0'..'9') setTransition(4, c, 4)
        setValue(2, TokenType.FLOAT)
        setValue(4, TokenType.FLOAT)
/*------------------------------------------------------------*/
        //variable [a-zA-Z_]+[a-zA-Z_0-9]*
        setVarTransition_(1, 5)
        setVarTransition(5, 6)
        setVarTransition(6, 6)

        setValue(5, TokenType.VARIABLE)
        setValue(6, TokenType.VARIABLE)
/*------------------------------------------------------------*/
        //plus
        setTransition(1, '+', 7)
        setValue(7, TokenType.PLUS)
/*------------------------------------------------------------*/
        //minus
        setTransition(1, '-', 8)
        setValue(8, TokenType.MINUS)
/*------------------------------------------------------------*/
        //times
        setTransition(1, '*', 9)
        setValue(9, TokenType.TIMES)
/*------------------------------------------------------------*/
        //divide
        setTransition(1, '/', 10)
        setValue(10, TokenType.DIVIDE)
/*------------------------------------------------------------*/
        //pow
        setTransition(1, '^', 11)
        setValue(11, TokenType.POW)
/*------------------------------------------------------------*/
        //lparen
        setTransition(1, '(', 12)
        setValue(12, TokenType.LPAREN)
/*------------------------------------------------------------*/
        //rparen
        setTransition(1, ')', 13)
        setValue(13, TokenType.RPAREN)
/*------------------------------------------------------------*/
        //ignored
        setTransition(1, ' ', 14) //my linux users love this aka \s in windows
        setTransition(1, '\n', 14)
        setTransition(1, '\t', 14)
        setTransition(1, '\r', 14)
        setValue(14, TokenType.IGNORE)
/*------------------------------------------------------------*/
        //semi ;
        setTransition(1, ';', 15)
        setValue(15, TokenType.SEMI)
/*------------------------------------------------------------*/
        //lsqbracket
        setTransition(1, '[', 16)
        setValue(16, TokenType.LSQBR)
/*------------------------------------------------------------*/
        //rsqbracket
        setTransition(1, ']', 17)
        setValue(17, TokenType.RSQBR)
/*------------------------------------------------------------*/
        //comma
        setTransition(1, ',', 18)
        setValue(18, TokenType.COMMA)
/*------------------------------------------------------------*/
        //colon
        setTransition(1, ':', 19)
        setValue(19, TokenType.COLON)
/*------------------------------------------------------------*/
        //lbracket
        setTransition(1, '{', 20)
        setValue(20, TokenType.LBR)
/*------------------------------------------------------------*/
        //rbracket
        setTransition(1, '}', 21)
        setValue(21, TokenType.RBR)
/*------------------------------------------------------------*/
        //not
        setTransition(1, '!', 22)
        setValue(22, TokenType.NOT)
/*------------------------------------------------------------*/
        //more
        setTransition(1, '>', 23)
        setValue(23, TokenType.MORE)
/*------------------------------------------------------------*/
        //less
        setTransition(1, '<', 24)
        setValue(24, TokenType.LESS)
/*------------------------------------------------------------*/
        //moreeq
        setTransition(23, '=', 25)
        setValue(25, TokenType.MOREEQ)
/*------------------------------------------------------------*/
        //lesseq
        setTransition(24, '=', 26)
        setValue(26, TokenType.LESSEQ)
/*------------------------------------------------------------*/
        //noteq
        setTransition(22, '=', 27)
        setValue(27, TokenType.NOTEQ)
/*------------------------------------------------------------*/
        //assign
        setTransition(1, '=', 28)
        setValue(28, TokenType.ASSIGN)
/*------------------------------------------------------------*/
        //eq
        setTransition(28, '=', 29)
        setValue(29, TokenType.EQ)
/*------------------------------------------------------------*/
        //lambda
        setTransition(28, '>', 30)
        setValue(30, TokenType.LAMBDA)
/*------------------------------------------------------------*/
        //line
        setTransition(8, '-', 31)
        setValue(31, TokenType.LINE)
/*------------------------------------------------------------*/
        //and
        setTransition(1, '&', 32)
        setTransition(32, '&', 33)
        setValue(33, TokenType.AND)
/*------------------------------------------------------------*/
        //or
        setTransition(1, '|', 34)
        setTransition(34, '|', 35)
        setValue(35, TokenType.OR)
/*------------------------------------------------------------*/
        //mod
        setTransition(1, '%', 36)
        setValue(36, TokenType.MOD)
/*------------------------------------------------------------*/
        //break
        setVarTransition(37, 6)
        setVarTransition(38, 6)
        setVarTransition(39, 6)
        setVarTransition(40, 6)
        setVarTransition(41, 6)
        setTransition(1, 'b', 37)
        setTransition(37, 'r', 38)
        setTransition(38, 'e', 39)
        setTransition(39, 'a', 40)
        setTransition(40, 'k', 41)
        setValue(37, TokenType.VARIABLE)
        setValue(38, TokenType.VARIABLE)
        setValue(39, TokenType.VARIABLE)
        setValue(40, TokenType.VARIABLE)
        setValue(41, TokenType.BREAK)
/*------------------------------------------------------------*/
        //continue
        setVarTransition(42, 6)
        setVarTransition(43, 6)
        setVarTransition(44, 6)
        setVarTransition(45, 6)
        setVarTransition(46, 6)
        setVarTransition(47, 6)
        setVarTransition(48, 6)
        setVarTransition(49, 6)
        setTransition(1, 'c', 42)
        setTransition(42, 'o', 43)
        setTransition(43, 'n', 44)
        setTransition(44, 't', 45)
        setTransition(45, 'i', 46)
        setTransition(46, 'n', 47)
        setTransition(47, 'u', 48)
        setTransition(48, 'e', 49)
        setValue(42, TokenType.VARIABLE)
        setValue(43, TokenType.VARIABLE)
        setValue(44, TokenType.VARIABLE)
        setValue(45, TokenType.VARIABLE)
        setValue(46, TokenType.VARIABLE)
        setValue(47, TokenType.VARIABLE)
        setValue(48, TokenType.VARIABLE)
        setValue(49, TokenType.CONTINUE)
/*------------------------------------------------------------*/
        //return
        setVarTransition(50, 6)
        setVarTransition(51, 6)
        setVarTransition(52, 6)
        setVarTransition(53, 6)
        setVarTransition(54, 6)
        setVarTransition(55, 6)
        setTransition(1, 'r', 50)
        setTransition(50, 'e', 51)
        setTransition(51, 't', 52)
        setTransition(52, 'u', 53)
        setTransition(53, 'r', 54)
        setTransition(54, 'n', 55)
        setValue(50, TokenType.VARIABLE)
        setValue(51, TokenType.VARIABLE)
        setValue(52, TokenType.VARIABLE)
        setValue(53, TokenType.VARIABLE)
        setValue(54, TokenType.VARIABLE)
        setValue(55, TokenType.RETURN)
/*------------------------------------------------------------*/
        //let
        setVarTransition(56, 6)
        setVarTransition(57, 6)
        setVarTransition(58, 6)
        setTransition(1, 'l', 56)
        setTransition(56, 'e', 57)
        setTransition(57, 't', 58)
        setValue(56, TokenType.VARIABLE)
        setValue(57, TokenType.VARIABLE)
        setValue(58, TokenType.LET)
/*------------------------------------------------------------*/
        //if
        setVarTransition(59, 6)
        setVarTransition(60, 6)
        setTransition(1, 'i', 59)
        setTransition(59, 'f', 60)
        setValue(59, TokenType.VARIABLE)
        setValue(60, TokenType.IF)
/*------------------------------------------------------------*/
        //else
        setVarTransition(61, 6)
        setVarTransition(62, 6)
        setVarTransition(63, 6)
        setVarTransition(64, 6)
        setTransition(1, 'e', 61)
        setTransition(61, 'l', 62)
        setTransition(62, 's', 63)
        setTransition(63, 'e', 64)
        setValue(61, TokenType.VARIABLE)
        setValue(62, TokenType.VARIABLE)
        setValue(63, TokenType.VARIABLE)
        setValue(64, TokenType.ELSE)
/*------------------------------------------------------------*/
        //in
        setVarTransition(65, 6)
        setTransition(59, 'n', 65)
        setValue(65, TokenType.IN)
/*------------------------------------------------------------*/
        //for
        setVarTransition(66, 6)
        setVarTransition(67, 6)
        setVarTransition(68, 6)
        setTransition(1, 'f', 66)
        setTransition(66, 'o', 67)
        setTransition(67, 'r', 68)
        setValue(66, TokenType.VARIABLE)
        setValue(67, TokenType.VARIABLE)
        setValue(68, TokenType.FOR)
/*------------------------------------------------------------*/
        //fn
        setVarTransition(69, 6)
        setTransition(66, 'n', 69)
        setValue(69, TokenType.FN)
/*------------------------------------------------------------*/
        //false
        setVarTransition(70, 6)
        setVarTransition(71, 6)
        setVarTransition(72, 6)
        setVarTransition(73, 6)
        setTransition(66, 'a', 70)
        setTransition(70, 'l', 71)
        setTransition(71, 's', 72)
        setTransition(72, 'e', 73)
        setValue(70, TokenType.VARIABLE)
        setValue(71, TokenType.VARIABLE)
        setValue(72, TokenType.VARIABLE)
        setValue(73, TokenType.FALSE)
/*------------------------------------------------------------*/
        //true
        setVarTransition(74, 6)
        setVarTransition(75, 6)
        setVarTransition(76, 6)
        setVarTransition(77, 6)
        setTransition(1, 't', 74)
        setTransition(74, 'r', 75)
        setTransition(75, 'u', 76)
        setTransition(76, 'e', 77)
        setValue(74, TokenType.VARIABLE)
        setValue(75, TokenType.VARIABLE)
        setValue(76, TokenType.VARIABLE)
        setValue(77, TokenType.TRUE)
/*------------------------------------------------------------*/
        //null
        setVarTransition(78, 6)
        setVarTransition(79, 6)
        setVarTransition(80, 6)
        setVarTransition(81, 6)
        setTransition(1, 'n', 78)
        setTransition(78, 'u', 79)
        setTransition(79, 'l', 80)
        setTransition(80, 'l', 81)
        setValue(78, TokenType.VARIABLE)
        setValue(79, TokenType.VARIABLE)
        setValue(80, TokenType.VARIABLE)
        setValue(81, TokenType.NULL)
/*------------------------------------------------------------*/
        // str0 -> r#"((.*)?)"#
        setTransition(50, '#', 82)
        setTransition(82, '"', 83)
        for (c in alphabet) setTransition(83, c.toChar(), 84)
        setTransition(83, '"', 85)
        for (c in alphabet) setTransition(84, c.toChar(), 84)
        setTransition(84, '"', 85)
        for (c in alphabet) setTransition(85, c.toChar(), 84)
        setTransition(85, '#', 86)
        setValue(82, TokenType.VARIABLE)
        setValue(86, TokenType.STR0)
/*------------------------------------------------------------*/
        // str1 -> "((.*)?)"
        setTransition(1, '"', 87)
        for (c in alphabet) setTransition(87, c.toChar(), 88)
        setTransition(87, '"', 89)
        for (c in alphabet) setTransition(88, c.toChar(), 88)
        setTransition(88, '"', 89)
        setValue(89, TokenType.STR1)
/*------------------------------------------------------------*/
        // str2 -> '((.*)?)'
        setTransition(1, '\'', 90)
        for (c in alphabet) setTransition(90, c.toChar(), 91)
        setTransition(90, '\'', 92)
        for (c in alphabet) setTransition(91, c.toChar(), 91)
        setTransition(91, '\'', 92)
        setValue(92, TokenType.STR2)
/*------------------------------------------------------------*/
        // scomment - //(.*?)\n?
        setTransition(10, '/', 93)
        for (c in alphabet) setTransition(93, c.toChar(), 94)
        setTransition(93, '\n', 95)
        for (c in alphabet) setTransition(94, c.toChar(), 94)
        setTransition(94, '\n', 95)
        setValue(93, TokenType.IGNORE)
        setValue(94, TokenType.IGNORE)
        setValue(95, TokenType.IGNORE)
/*------------------------------------------------------------*/
        // mcomment -> /*((.*)?)*/
        setTransition(10, '*', 96)
        for (c in alphabet) setTransition(96, c.toChar(), 97)
        setTransition(96, '*', 98)
        for (c in alphabet) setTransition(97, c.toChar(), 97)
        setTransition(97, '*', 98)
        for (c in alphabet) setTransition(98, c.toChar(), 97)
        setTransition(98, '/', 99)
        setValue(96, TokenType.IGNORE)
        setValue(97, TokenType.IGNORE)
        setValue(98, TokenType.IGNORE)
        setValue(99, TokenType.IGNORE)
    }
}