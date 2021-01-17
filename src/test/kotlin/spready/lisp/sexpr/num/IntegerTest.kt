package spready.lisp.sexpr.num

import org.junit.jupiter.api.Nested
import spready.lisp.sexpr.Flt
import spready.lisp.sexpr.Fraction
import spready.lisp.sexpr.Integer
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegerTest {
    @Test
    fun invert() {
        assertEquals(Fraction.create(1, 10), Integer(10).invert())
    }

    @Test
    fun `unary minus`() {
        assertEquals(Integer(-10), -Integer(10))
    }

    @Nested
    inner class Conversion {
        @Test
        fun `toFlt normal`() {
            assertEquals(123.0, Integer(123).toFlt().value)
        }

        @Test
        fun `toInteger normal`() {
            assertEquals(123, Integer(123).toInteger().value)
        }

        @Test
        fun `toFraction normal`() {
            val fraction = Integer(123).toFraction()

            assertEquals(123, fraction.numerator)
            assertEquals(1, fraction.denominator)
        }
    }

    @Nested
    inner class Compare {
        @Test
        fun `compareTo Integer`() {
            assertEquals(true, Integer(4) < Integer(10))
        }

        @Test
        fun `compareTo Float`() {
            assertEquals(true, Integer(4) < Flt(4.5))
        }

        @Test
        fun `compareTo Fraction`() {
            assertEquals(true, Integer(1) < Fraction.create(3, 2))
        }
    }

    @Nested
    inner class Equals {
        @Test
        fun `equals Integer`() {
            assertEquals(true, Integer(4) == Integer(4))
        }

        @Test
        fun `equals Float`() {
            assertEquals(true, Integer(4).equals(Flt(4.0)))
        }

        @Test
        fun `equals Fraction`() {
            assertEquals(true, Integer(4).equals(Fraction.create(8, 2)))
        }

        @Test
        fun `equals Any`() {
            assertEquals(false, Integer(1) == ("Test" as Any))
        }

        @Test
        fun `equals null`() {
            assertEquals(false, Integer(1).equals(null))
        }
    }

    @Nested
    inner class Plus {
        @Test
        fun `plus Integer`() {
            assertEquals(Integer(10), Integer(3) + Integer(7))
        }

        @Test
        fun `plus Float`() {
            assertEquals(Flt(10.0), Integer(3) + Flt(7.0))
        }

        @Test
        fun `plus Fraction`() {
            assertEquals(
                Fraction.create(15, 4),
                Integer(3) + Fraction.create(3, 4)
            )
        }
    }

    @Nested
    inner class Times {
        @Test
        fun `times Integer`() {
            assertEquals(Integer(21), Integer(3) * Integer(7))
        }

        @Test
        fun `times Float`() {
            assertEquals(Flt(21.0), Integer(3) * Flt(7.0))
        }

        @Test
        fun `times Fraction`() {
            assertEquals(
                Fraction.create(9, 4),
                Integer(3) * Fraction.create(3, 4)
            )
        }
    }
}
