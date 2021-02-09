package spready.lisp

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import spready.lisp.functions.forms.Quasiquote
import spready.lisp.functions.forms.Quote
import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Flt
import spready.lisp.sexpr.Fraction
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Str
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.Unquote
import spready.lisp.sexpr.UnquoteSplice
import java.util.stream.Stream
import kotlin.math.E
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParserTest {

    @Nested
    inner class ParseCons {
        @Test
        fun `parse Cons empty List`() {
            val input: List<Token> = listOf()

            assertThrows<IllegalArgumentException> {
                parseOther(input.toMutableList())
            }
        }

        @Test
        fun `parse Cons Unbalanced Parens`() {
            val input = listOf(
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "123"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.CloseParen, ")")
            )

            assertThrows<EvalException> {
                parseOther(input.toMutableList())
            }
        }

        @Test
        fun `parse Cons only )`() {
            val input = listOf(
                Token(TokenType.CloseParen, ")")
            )

            assertThrows<EvalException> {
                parseOther(input.toMutableList())
            }
        }

        @Test
        fun `parse Cons successfully`() {
            val input = listOf(
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "123"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "456"),
                Token(TokenType.String, "789"),
                Token(TokenType.CloseParen, ")"),
                Token(TokenType.CloseParen, ")")
            )

            val expected =
                Cons(Integer(123), Cons(Cons(Integer(456), Cons(Str("789"), Nil)), Nil))

            val parsed = parseOther(input.toMutableList())
            assertEquals(expected, parsed)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ParseAtom {
        private fun parseAtomProvider() =
            Stream.of(
                Pair(Token(TokenType.String, "123"), Str("123")),
                Pair(Token(TokenType.Symbol, "123"), Integer(123)),
                Pair(Token(TokenType.Symbol, "hallo"), Symbol("hallo")),
                Pair(Token(TokenType.Symbol, "-123"), Integer(-123)),
                Pair(Token(TokenType.Symbol, "nil"), Nil),
                Pair(Token(TokenType.Symbol, "-1.123"), Flt(-1.123)),
                Pair(Token(TokenType.Symbol, "1/2"), Fraction.create(1, 2)),
                Pair(Token(TokenType.Symbol, "-1/2"), Fraction.create(-1, 2)),
            )

        @ParameterizedTest
        @MethodSource("parseAtomProvider")
        fun `parse Atom successfully`(data: Pair<Token, SExpr>) {
            assertEquals(data.second, parseAtom(data.first))
        }

        @Test
        fun `parse Atom fail`() {
            assertThrows<IllegalArgumentException> {
                parseAtom(Token(TokenType.OpenParen, "("))
            }
            assertThrows<IllegalArgumentException> {
                parseAtom(Token(TokenType.CloseParen, ")"))
            }
        }
    }

    @Nested
    inner class ParseSpecial {

        @Test
        fun `parse special fail`() {
            assertFailsWith<EvalException> {
                parse(listOf(Token(TokenType.Special, "#123")))
            }
        }

        @Test
        fun `parse bool`() {
            assertEquals(
                listOf(Bool(true)),
                parse(listOf(Token(TokenType.Special, "#t")))
            )
            assertEquals(
                listOf(Bool(false)),
                parse(listOf(Token(TokenType.Special, "#f")))
            )
        }

        @Test
        fun `parse math`() {
            assertEquals(
                listOf(Flt(E)),
                parse(listOf(Token(TokenType.Special, "#e")))
            )

            assertEquals(
                listOf(Flt(PI)),
                parse(listOf(Token(TokenType.Special, "#pi")))
            )
        }

        @Test
        fun `parse Cell`() {
            assertEquals(
                listOf(Cell(123, 2)),
                parse(listOf(Token(TokenType.Special, "#123.2")))
            )
        }

        @Test
        fun `parse Cell fail`() {
            assertFailsWith<EvalException> {
                parse(listOf(Token(TokenType.Special, "#-12.32")))
            }
        }
    }

    @Nested
    inner class ParseQuotes {
        @Test
        fun `parse Quotes`() {
            val input = listOf(
                Token(TokenType.Quote, "'"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "123"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "456"),
                Token(TokenType.String, "789"),
                Token(TokenType.CloseParen, ")"),
                Token(TokenType.CloseParen, ")")
            )

            val expected =
                Cons(
                    Quote,
                    Cons(
                        Cons(
                            Integer(123),
                            Cons(Cons(Integer(456), Cons(Str("789"), Nil)), Nil)
                        ),
                        Nil
                    )
                )

            val parsed = parseOther(input.toMutableList())
            assertEquals(expected, parsed)
        }

        @Test
        fun `parse QuasiQuotes`() {
            val input = listOf(
                Token(TokenType.QuasiQuote, "`"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "123"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "456"),
                Token(TokenType.String, "789"),
                Token(TokenType.CloseParen, ")"),
                Token(TokenType.CloseParen, ")")
            )

            val expected =
                Cons(
                    Quasiquote,
                    Cons(
                        Cons(
                            Integer(123),
                            Cons(Cons(Integer(456), Cons(Str("789"), Nil)), Nil)
                        ),
                        Nil
                    )
                )

            val parsed = parseOther(input.toMutableList())
            assertEquals(expected, parsed)
        }

        @Test
        fun `parse Unquote`() {
            val input = listOf(
                Token(TokenType.Unquote, ","),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "123"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "456"),
                Token(TokenType.String, "789"),
                Token(TokenType.CloseParen, ")"),
                Token(TokenType.CloseParen, ")")
            )

            val expected =
                Cons(
                    Unquote,
                    Cons(
                        Cons(
                            Integer(123),
                            Cons(Cons(Integer(456), Cons(Str("789"), Nil)), Nil)
                        ),
                        Nil
                    )
                )

            val parsed = parseOther(input.toMutableList())
            assertEquals(expected, parsed)
        }

        @Test
        fun `parse UnquoteSplice`() {
            val input = listOf(
                Token(TokenType.UnquoteSplice, ",@"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "123"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "456"),
                Token(TokenType.String, "789"),
                Token(TokenType.CloseParen, ")"),
                Token(TokenType.CloseParen, ")")
            )

            val expected =
                Cons(
                    UnquoteSplice,
                    Cons(
                        Cons(
                            Integer(123),
                            Cons(Cons(Integer(456), Cons(Str("789"), Nil)), Nil)
                        ),
                        Nil
                    )
                )

            val parsed = parseOther(input.toMutableList())
            assertEquals(expected, parsed)
        }
    }

    @Nested
    inner class Parse {
        @Test
        fun `parse just Atom`() {
            val input = listOf(Token(TokenType.Symbol, "123"))

            assertEquals(listOf(Integer(123)), parse(input))
        }

        @Test
        fun `parse multiple`() {
            val input = listOf(
                Token(TokenType.Symbol, "123"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "123"),
                Token(TokenType.CloseParen, ")")
            )

            assertEquals(listOf(Integer(123), Cons(Integer(123), Nil)), parse(input))
        }

        @Test
        fun `parse empty list`() {
            assertEquals(listOf(), parse(listOf()))
        }

        @Test
        fun `parse unexpected token`() {
            assertThrows<EvalException> {
                parse(listOf(Token(TokenType.CloseParen, ")")))
            }
        }
    }

    @Nested
    inner class ParseDot {
        @Test
        fun `parse only dot fail`() {
            val input = listOf(
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Dot, "."),
                Token(TokenType.CloseParen, ")")
            )

            assertThrows<IllegalArgumentException> {
                parse(input)
            }
        }

        @Test
        fun `parse one elem with dot fail`() {
            val input = listOf(
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Special, "123"),
                Token(TokenType.Dot, "."),
                Token(TokenType.CloseParen, ")")
            )

            assertThrows<EvalException> {
                parse(input)
            }
        }

        @Test
        fun `parse dot normal`() {
            val input = listOf(
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "123"),
                Token(TokenType.Symbol, "456"),
                Token(TokenType.Dot, "."),
                Token(TokenType.Symbol, "789"),
                Token(TokenType.CloseParen, ")")
            )

            assertEquals(
                Cons(Integer(123), Cons(Integer(456), Integer(789))),
                parse(input).first()
            )
        }
    }
}
