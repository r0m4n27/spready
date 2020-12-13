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
            Token(TokenType.Number, "1"),
            Token(TokenType.Number, "2"),
            Token(TokenType.Number, "3"),
            Token(TokenType.CloseParen, ")")
        )

        assertEquals(expected, tokenize(input))
    }

    @Test
    fun `tokenize number`() {
        val input = "1234"
        val expected = listOf(Token(TokenType.Number, "1234"))
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
            Token(TokenType.Number, "132"),
            Token(TokenType.Number, "4"),
            Token(TokenType.CloseParen, ")")
        )

        assertEquals(expected, tokenize(input))
    }
}
