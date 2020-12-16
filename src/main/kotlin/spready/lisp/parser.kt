package spready.lisp

// TODO: Parse List of exprs
fun parse(tokens: List<Token>): SExpr {
    val firstItem = tokens.firstOrNull()

    return if (firstItem != null) {
        if (firstItem.type.isAtom) {
            parseAtom(firstItem)
        } else {
            if (firstItem.type == TokenType.CloseParen) {
                throw IllegalArgumentException("Unexpected token $firstItem")
            }

            parseCons(tokens.toMutableList())
        }
    } else {
        throw IllegalArgumentException("No Tokens were Provided")
    }
}

fun parseAtom(token: Token): SExpr {
    return when (token.type) {
        TokenType.Symbol -> Symbol(token.value)
        TokenType.String -> Str(token.value)
        TokenType.Number -> Num(token.value.toInt())
        else -> throw IllegalArgumentException("$token isn't an atom!")
    }
}

fun parseCons(tokens: MutableList<Token>): SExpr {
    if (tokens.isNotEmpty()) {
        val first = tokens.removeFirst()

        return when {
            first.type.isAtom -> {
                return parseAtom(first)
            }
            first.type == TokenType.OpenParen -> {
                val exprs: MutableList<SExpr> = mutableListOf()
                while (tokens.isNotEmpty() &&
                    tokens.first().type != TokenType.CloseParen
                ) {
                    exprs.add(parseCons(tokens))
                }

                if (tokens.isEmpty()) {
                    throw IllegalArgumentException("Parenthesis weren't balanced!")
                }

                tokens.removeFirst()

                buildCons(exprs)
            }
            else -> {
                throw IllegalArgumentException("Unexpected token $first")
            }
        }
    } else {
        throw IllegalArgumentException("Tokens can't be empty!")
    }
}

fun buildCons(exprs: MutableList<SExpr>): SExpr {
    return if (exprs.isEmpty()) {
        Nil
    } else {
        Cons(exprs.removeFirst(), buildCons(exprs))
    }
}
