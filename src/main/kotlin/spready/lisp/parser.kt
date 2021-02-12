package spready.lisp

import spready.lisp.functions.forms.Quasiquote
import spready.lisp.functions.forms.Quote
import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Cell
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
import kotlin.math.E
import kotlin.math.PI

/**
 * Parses the [tokens] and gives a list of [SExpr]
 *
 * @throws EvalException if the parse fails
 */
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
        return listOf()
    }
}

/**
 * Parses a [TokenType.String], [TokenType.Special] or [TokenType.Symbol]
 *
 * @throws IllegalArgumentException if the token is not one of the three
 */
fun parseAtom(token: Token): SExpr {
    return when (token.type) {
        TokenType.String -> Str(token.value)
        TokenType.Special -> parseSpecial(token.value)
        TokenType.Symbol -> parseSymbol(token.value)
        else -> throw IllegalArgumentException("$token isn't an atom!")
    }
}

/**
 * Parses a symbol token
 *
 * Can be [Nil], [Fraction], [Integer], [Flt] or [Symbol]
 */
fun parseSymbol(value: String): SExpr {
    if (value == "nil") {
        return Nil
    }

    val fractionRegex = Regex("""(-?\d+)/(\d+)""")

    val match = fractionRegex.matchEntire(value)
    if (match != null) {
        val (num, den) = match.destructured
        return Fraction.create(num.toInt(), den.toInt())
    }

    return try {
        Integer(value.toInt())
    } catch (_: NumberFormatException) {
        try {
            Flt(value.toDouble())
        } catch (_: NumberFormatException) {
            Symbol(value)
        }
    }
}

/**
 * Parses the special Token
 *
 * Values can be: true, false, pi, e or a cell
 *
 * @throws EvalException when the special value isn't recognised
 */
fun parseSpecial(value: String): SExpr {
    return when (value) {
        "#t" -> Bool(true)
        "#f" -> Bool(false)
        "#pi" -> Flt(PI)
        "#e" -> Flt(E)
        else -> {
            val cellRegex = Regex("""#(\d+)\.(\d+)""")
            val match = cellRegex.matchEntire(value)

            if (match != null) {
                val (row, col) = match.destructured

                return Cell(row.toInt(), col.toInt())
            }

            throw EvalException("Can't parse Special $value!")
        }
    }
}

/**
 * Parses a structure
 *
 * Parses the parenthesis recursively
 *
 * @throws IllegalArgumentException if [tokens] is empty
 * @throws EvalException if the parenthesis weren't balanced
 * or unexpected token was found
 */
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
                throw EvalException("Parenthesis weren't balanced!")
            }

            tokens.removeFirst()

            if (isDot) {
                exprs.toConsWithTail()
            } else {
                exprs.toListElem()
            }
        }

        TokenType.Quote -> Cons(Quote, Cons(parseOther(tokens), Nil))
        TokenType.QuasiQuote -> Cons(Quasiquote, Cons(parseOther(tokens), Nil))
        TokenType.Unquote -> Cons(Unquote, Cons(parseOther(tokens), Nil))
        TokenType.UnquoteSplice -> Cons(UnquoteSplice, Cons(parseOther(tokens), Nil))

        else -> {
            if (first.type.isAtom) {
                parseAtom(first)
            } else {
                throw EvalException("Unexpected token $first")
            }
        }
    }
}
