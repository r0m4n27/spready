package spready.lisp

import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Cons.Companion.toCons
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.Num
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Str
import spready.lisp.sexpr.Symbol

fun parse(tokens: List<Token>): List<SExpr> {

    if (tokens.isNotEmpty()) {
        val mutableTokens = tokens.toMutableList()
        val expressions = mutableListOf<SExpr>()

        while (mutableTokens.isNotEmpty()) {
            val firstItem = mutableTokens.first()

            val expr = if (firstItem.type.isAtom) {
                parseAtom(mutableTokens.removeFirst())
            } else {
                if (firstItem.type == TokenType.CloseParen) {
                    throw IllegalArgumentException("Unexpected token $firstItem")
                }

                parseCons(mutableTokens)
            }

            expressions.add(expr)
        }

        return expressions
    } else {
        throw IllegalArgumentException("No Tokens were Provided")
    }
}

fun parseAtom(token: Token): SExpr {
    return when (token.type) {
        TokenType.String -> Str(token.value)
        TokenType.Special -> parseSpecial(token)
        TokenType.Symbol -> {
            if (token.value == "nil") {
                Nil
            } else {

                try {
                    Num(token.value.toInt())
                } catch (_: NumberFormatException) {
                    Symbol(token.value)
                }
            }
        }
        else -> throw IllegalArgumentException("$token isn't an atom!")
    }
}

fun parseSpecial(token: Token): SExpr {
    return when (token.value) {
        "#t" -> Bool(true)
        "#f" -> Bool(false)
        else -> throw IllegalArgumentException("Can't parse Special Token $token")
    }
}

fun parseCons(tokens: MutableList<Token>): SExpr {
    if (tokens.isNotEmpty()) {
        val first = tokens.removeFirst()

        return when {
            first.type.isAtom -> {
                parseAtom(first)
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

                exprs.toCons()
            }
            else -> {
                throw IllegalArgumentException("Unexpected token $first")
            }
        }
    } else {
        throw IllegalArgumentException("Tokens can't be empty!")
    }
}
