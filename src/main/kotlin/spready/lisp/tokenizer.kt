package spready.lisp

enum class TokenType(val isAtom: Boolean) {
    OpenParen(false),
    CloseParen(false),
    String(true),
    Symbol(true),
    Number(true)
}

data class Token(val type: TokenType, val value: String)

// TODO: Can't Handle Symbols at the end 'x)'
fun tokenize(input: String): List<Token> {
    var s = input.trimStart()
    val tokens: MutableList<Token> = mutableListOf()

    while (s.isNotEmpty()) {
        var dropAnother = false

        val token = when (val firstChar = s[0]) {
            '(' -> Token(TokenType.OpenParen, "(")
            ')' -> Token(TokenType.CloseParen, ")")
            '\"' -> {
                s = s.drop(1)
                val inQuotes = s.takeWhile { it != '\"' }
                dropAnother = true
                Token(TokenType.String, inQuotes)
            }
            '\n' -> {
                dropAnother = true
                null
            }
            else -> {
                // TODO: Can't recognise negative numbers
                if (firstChar.isDigit()) {

                    val num = s.takeWhile { it.isDigit() }
                    Token(TokenType.Number, num)
                } else {

                    val symbol = s.takeWhile { it != ' ' }
                    Token(TokenType.Symbol, symbol)
                }
            }
        }

        var dropLength = 0
        if (token != null) {
            tokens.add(token)
            dropLength = token.value.length
        }

        if (dropAnother) dropLength += 1

        s = s.drop(dropLength).trimStart()
    }

    return tokens
}
