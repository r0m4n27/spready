package spready.lisp

enum class TokenType(val isAtom: Boolean) {
    OpenParen(false),
    CloseParen(false),
    String(true),
    Special(true),
    Symbol(true)
}

data class Token(val type: TokenType, val value: String)

fun tokenize(input: String): List<Token> {
    val specialChars = arrayOf('(', ')', ' ')

    var s = input.trimStart()
    val tokens: MutableList<Token> = mutableListOf()

    while (s.isNotEmpty()) {

        val token = when (s[0]) {
            '(' -> Token(TokenType.OpenParen, "(")
            ')' -> Token(TokenType.CloseParen, ")")
            '\"' -> {
                s = s.drop(1)
                val inQuotes = s.takeWhile { it != '\"' }
                s = s.drop(1)
                Token(TokenType.String, inQuotes)
            }
            '\n' -> {
                s = s.drop(1)
                null
            }
            '#' -> {
                val special = s.takeWhile { !specialChars.contains(it) }
                Token(TokenType.Special, special)
            }

            else -> {
                val symbol = s.takeWhile { !specialChars.contains(it) }
                Token(TokenType.Symbol, symbol)
            }
        }

        val dropLength = if (token != null) {
            tokens.add(token)
            token.value.length
        } else {
            0
        }

        s = s.drop(dropLength).trimStart()
    }

    return tokens
}
