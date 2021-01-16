package spready.lisp

import spready.lisp.functions.forms.Quasiquote
import spready.lisp.functions.forms.Quote
import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Flt
import spready.lisp.sexpr.Fraction
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.ListElem.Companion.toConsWithTail
import spready.lisp.sexpr.ListElem.Companion.toListElem
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Str
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.Unquote
import spready.lisp.sexpr.UnquoteSplice

fun parse(tokens: List<Token>): List<SExpr> {

    if (tokens.isNotEmpty()) {
        val mutableTokens = tokens.toMutableList()
        val expressions = mutableListOf<SExpr>()

        while (mutableTokens.isNotEmpty()) {
            val firstItem = mutableTokens.first()

            val expr = if (firstItem.type.isAtom) {
                parseAtom(mutableTokens.removeFirst())
            } else {
                parseOther(mutableTokens)
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
        TokenType.Special -> parseSpecial(token.value)
        TokenType.Symbol -> parseSymbol(token.value)
        else -> throw IllegalArgumentException("$token isn't an atom!")
    }
}

fun parseSymbol(value: String): SExpr {
    if (value == "nil") {
        return Nil
    }

    val floatRegex = Regex("""-?\d+\.\d+""")
    val fractionRegex = Regex("""(-?\d+)/(\d+)""")

    if (value.matches(floatRegex)) {
        return Flt(value.toDouble())
    }

    val match = fractionRegex.matchEntire(value)
    if (match != null) {
        val (num, den) = match.destructured
        return Fraction.create(num.toInt(), den.toInt())
    }

    return try {
        Integer(value.toInt())
    } catch (_: NumberFormatException) {
        Symbol(value)
    }
}

fun parseSpecial(value: String): SExpr {
    return when (value) {
        "#t" -> Bool(true)
        "#f" -> Bool(false)
        else -> throw IllegalArgumentException("Can't parse Special $value!")
    }
}

fun parseOther(tokens: MutableList<Token>): SExpr {
    if (tokens.isEmpty()) {
        throw IllegalArgumentException("Tokens can't be empty!")
    }

    val first = tokens.removeFirst()

    return when (first.type) {
        TokenType.OpenParen -> {
            val exprs: MutableList<SExpr> = mutableListOf()
            var isDot = false

            while (tokens.isNotEmpty() &&
                tokens.first().type != TokenType.CloseParen
            ) {
                if (tokens.first().type == TokenType.Dot) {
                    isDot = true
                    tokens.removeFirst()
                } else {
                    exprs.add(parseOther(tokens))
                }
            }

            if (tokens.isEmpty()) {
                throw IllegalArgumentException("Parenthesis weren't balanced!")
            }

            tokens.removeFirst()

            if (isDot) {
                exprs.toConsWithTail()
            } else {
                exprs.toListElem()
            }
        }

        TokenType.Quote -> Cons(Quote, Cons(parseOther(tokens), Nil))
        TokenType.Quasiquote -> Cons(Quasiquote, Cons(parseOther(tokens), Nil))
        TokenType.Unquote -> Cons(Unquote, Cons(parseOther(tokens), Nil))
        TokenType.UnquoteSplice -> Cons(UnquoteSplice, Cons(parseOther(tokens), Nil))

        else -> {
            if (first.type.isAtom) {
                parseAtom(first)
            } else {
                throw IllegalArgumentException("Unexpected token $first")
            }
        }
    }
}
