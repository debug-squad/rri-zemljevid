package si.feri.dsl.lexer

enum class TokenType(val value: Int) {
    IGNORE(0),
    FLOAT(1),
    VARIABLE(2),
    PLUS(3),
    MINUS(4),
    TIMES(5),
    DIVIDE(6),
    POW(7),
    LPAREN(8),
    RPAREN(9),
    SEMI(10),
    LSQBR(11),
    RSQBR(12),
    COMMA(13),
    COLON(14),
    LBR(15),
    RBR(16),
    NOT(17),
    MORE(18),
    LESS(19),
    MOREEQ(20),
    LESSEQ(21),
    NOTEQ(22),
    ASSIGN(23),
    EQ(24),
    LAMBDA(25),
    LINE(26),
    AND(27),
    OR(28),
    MOD(29),
    BREAK(30),
    CONTINUE(31),
    RETURN(32),
    LET(33),
    IF(34),
    ELSE(35),
    IN(36),
    FOR(37),
    FN(38),
    FALSE(39),
    TRUE(40),
    NULL(41),
    STR0(42),
    STR1(43),
    STR2(44);

    companion object {
        fun name(value: Int) =
            when (value) {
                IGNORE.value -> "ignore"
                FLOAT.value -> "float"
                VARIABLE.value -> "variable"
                PLUS.value -> "plus"
                MINUS.value -> "minus"
                TIMES.value -> "times"
                DIVIDE.value -> "divide"
                POW.value -> "pow"
                LPAREN.value -> "lparen"
                RPAREN.value -> "rparen"
                SEMI.value -> "semi"
                LSQBR.value -> "lsqbr"
                RSQBR.value -> "rsqbr"
                COMMA.value -> "comma"
                COLON.value -> "colon"
                LBR.value -> "lbr"
                RBR.value -> "rbr"
                NOT.value -> "not"
                MORE.value -> "more"
                LESS.value -> "less"
                MOREEQ.value -> "moreeq"
                LESSEQ.value -> "lesseq"
                NOTEQ.value -> "noteq"
                ASSIGN.value -> "assign"
                EQ.value -> "eq"
                LAMBDA.value -> "lambda"
                LINE.value -> "line"
                AND.value -> "and"
                OR.value -> "or"
                MOD.value -> "mod"
                BREAK.value -> "break"
                CONTINUE.value -> "continue"
                RETURN.value -> "return"
                LET.value -> "let"
                IF.value -> "if"
                ELSE.value -> "else"
                IN.value -> "in"
                FOR.value -> "for"
                FN.value -> "fn"
                FALSE.value -> "false"
                TRUE.value -> "true"
                NULL.value -> "null"
                STR0.value -> "str0"
                STR1.value -> "str1"
                STR2.value -> "str2"

                else -> throw Error("Invalid value")
            }
    }
}