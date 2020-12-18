package spready.lisp

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParserTest {
    @Nested
    inner class BuildCons {
        @Test
        fun `build cons empty`() {
            val emptyList: List<SExpr> = listOf()

            assertEquals(Nil, buildCons(emptyList.toMutableList()))
        }

        @Test
        fun `build cons normal`() {
            val values = listOf(Num(123), Nil, Str("123"))

            var cons = buildCons(values.toMutableList())
            values.forEach {
                assertEquals(it, (cons as Cons).first)
                cons = (cons as Cons).second
            }

            assertEquals(Nil, cons as Nil)
        }
    }

    @Nested
    inner class ParseCons {
        @Test
        fun `parse Cons empty List`() {
            val input: List<Token> = listOf()

            assertThrows<IllegalArgumentException> {
                parseCons(input.toMutableList())
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

            assertThrows<IllegalArgumentException> {
                parseCons(input.toMutableList())
            }
        }

        @Test
        fun `parse Cons only )`() {
            val input = listOf(
                Token(TokenType.CloseParen, ")")
            )

            assertThrows<IllegalArgumentException> {
                parseCons(input.toMutableList())
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
                Cons(Num(123), Cons(Cons(Num(456), Cons(Str("789"), Nil)), Nil))

            val parsed = parseCons(input.toMutableList())
            assertEquals(expected, parsed)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ParseAtom {
        private fun parseAtomProvider() =
            Stream.of(
                Pair(Token(TokenType.String, "123"), Str("123")),
                Pair(Token(TokenType.Symbol, "123"), Num(123)),
                Pair(Token(TokenType.Symbol, "hallo"), Symbol("hallo")),
                Pair(Token(TokenType.Symbol, "-123"), Num(-123))
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
        fun `parse special success`() {
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
        fun `parse special fail`() {
            assertFailsWith<IllegalArgumentException> {
                parse(listOf(Token(TokenType.Special, "#123")))
            }
        }
    }

    @Nested
    inner class Parse {
        @Test
        fun `parse just Atom`() {
            val input = listOf(Token(TokenType.Symbol, "123"))

            assertEquals(listOf(Num(123)), parse(input))
        }

        @Test
        fun `parse multiple`() {
            val input = listOf(
                Token(TokenType.Symbol, "123"),
                Token(TokenType.OpenParen, "("),
                Token(TokenType.Symbol, "123"),
                Token(TokenType.CloseParen, ")")
            )

            assertEquals(listOf(Num(123), Cons(Num(123), Nil)), parse(input))
        }

        @Test
        fun `parse empty list`() {
            assertThrows<IllegalArgumentException> {
                parse(listOf())
            }
        }

        @Test
        fun `parse unexpected token`() {
            assertThrows<IllegalArgumentException> {
                parse(listOf(Token(TokenType.CloseParen, ")")))
            }
        }
    }
}