package spready.lisp

import kotlin.test.Test
import kotlin.test.assertEquals

class TokenizerTest {
    @Test
    fun `tokenize empty String`() {
        assertEquals(listOf(), tokenize(""))
    }

    @Test
    fun `tokenize normal`() {
        val input = "(1 2 3)"
        val expected = listOf(
            Token(TokenType.OpenParen, "("),
            Token(TokenType.Symbol, "1"),
            Token(TokenType.Symbol, "2"),
            Token(TokenType.Symbol, "3"),
            Token(TokenType.CloseParen, ")")
        )

        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize Symbol`() {
        val input = "-1234"
        val expected = listOf(Token(TokenType.Symbol, "-1234"))
        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize Special`() {
        val input = "#123"
        val expected = listOf(Token(TokenType.Special, "#123"))
        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize dot`() {
        val input = "."
        val expected = listOf(Token(TokenType.Dot, "."))
        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize String`() {
        val input = "\"Hallo, 123!\""

        val expected = listOf(Token(TokenType.String, "Hallo, 123!"))
        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize spaces and newlines`() {
        val input = "     (     132 \n  4)"

        val expected = listOf(
            Token(TokenType.OpenParen, "("),
            Token(TokenType.Symbol, "132"),
            Token(TokenType.Symbol, "4"),
            Token(TokenType.CloseParen, ")")
        )

        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize before special char`() {
        val input = "1 2(3 4)"

        val expected = listOf(
            Token(TokenType.Symbol, "1"),
            Token(TokenType.Symbol, "2"),
            Token(TokenType.OpenParen, "("),
            Token(TokenType.Symbol, "3"),
            Token(TokenType.Symbol, "4"),
            Token(TokenType.CloseParen, ")")
        )

        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize Quote`() {
        val input = "1 2 '(3 4)"

        val expected = listOf(
            Token(TokenType.Symbol, "1"),
            Token(TokenType.Symbol, "2"),
            Token(TokenType.Quote, "'"),
            Token(TokenType.OpenParen, "("),
            Token(TokenType.Symbol, "3"),
            Token(TokenType.Symbol, "4"),
            Token(TokenType.CloseParen, ")")
        )

        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize Quasiquote`() {
        val input = "1 2 `(3 4)"

        val expected = listOf(
            Token(TokenType.Symbol, "1"),
            Token(TokenType.Symbol, "2"),
            Token(TokenType.Quasiquote, "`"),
            Token(TokenType.OpenParen, "("),
            Token(TokenType.Symbol, "3"),
            Token(TokenType.Symbol, "4"),
            Token(TokenType.CloseParen, ")")
        )

        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize Unquote`() {
        val input = "1 2 `(3 ,4)"

        val expected = listOf(
            Token(TokenType.Symbol, "1"),
            Token(TokenType.Symbol, "2"),
            Token(TokenType.Quasiquote, "`"),
            Token(TokenType.OpenParen, "("),
            Token(TokenType.Symbol, "3"),
            Token(TokenType.Unquote, ","),
            Token(TokenType.Symbol, "4"),

            Token(TokenType.CloseParen, ")")
        )

        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize UnquoteSplice`() {
        val input = "1 2 `(3 ,@4)"

        val expected = listOf(
            Token(TokenType.Symbol, "1"),
            Token(TokenType.Symbol, "2"),
            Token(TokenType.Quasiquote, "`"),
            Token(TokenType.OpenParen, "("),
            Token(TokenType.Symbol, "3"),
            Token(TokenType.UnquoteSplice, ",@"),
            Token(TokenType.Symbol, "4"),
            Token(TokenType.CloseParen, ")")
        )

        assertEquals(expected, tokenize(input))
    }
}
