package spready.lisp.sexpr.num

import org.junit.jupiter.api.Nested
import spready.lisp.EvalException
import spready.lisp.sexpr.Flt
import spready.lisp.sexpr.Fraction
import spready.lisp.sexpr.Integer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FractionTest {
    @Test
    fun invert() {
        assertEquals(Fraction.create(2, 3), Fraction.create(3, 2).invert())
    }

    @Test
    fun `unary minus`() {
        assertEquals(Fraction.create(-3, 2), -Fraction.create(3, 2))
    }

    @Test
    fun abs() {
        assertEquals(Fraction.create(3, 2), Fraction.create(-3, 2).abs())
    }

    @Nested
    inner class Conversion {
        @Test
        fun `toFlt normal`() {
            assertEquals(1.5, Fraction.create(3, 2).toFlt().value)
        }

        @Test
        fun `toInteger normal`() {
            assertEquals(1, Fraction.create(3, 2).toInteger().value)
        }

        @Test
        fun `toFraction normal`() {
            val fraction = Fraction.create(6, 4).toFraction()

            assertEquals(3, fraction.numerator)
            assertEquals(2, fraction.denominator)
        }
    }

    @Nested
    inner class Compare {
        @Test
        fun `compareTo Fraction`() {
            assertEquals(
                true,
                Fraction.create(1, 2) < Fraction.create(2, 3)
            )
        }
    }

    @Nested
    inner class Equals {
        @Test
        fun `equals Fraction`() {
            assertEquals(true, Fraction.create(4, 1) == Fraction.create(8, 2))
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
    inner class Create {
        @Test
        fun `create normal`() {
            val fraction = Fraction.create(-32, 6)

            assertEquals(-16, fraction.numerator)
            assertEquals(3, fraction.denominator)
        }

        @Test
        fun `create fail neg`() {
            assertFailsWith<EvalException> {
                Fraction.create(3, -2)
            }
        }

        @Test
        fun `create fail 0`() {
            assertFailsWith<EvalException> {
                Fraction.create(3, 0)
            }
        }
    }

    @Nested
    inner class Plus {

        @Test
        fun `plus Fraction`() {
            assertEquals(
                Fraction.create(106, 21),
                Fraction.create(13, 3) + Fraction.create(5, 7)
            )
        }
    }

    @Nested
    inner class Times {

        @Test
        fun `times Fraction`() {
            assertEquals(
                Fraction.create(13 * 5, 21),
                Fraction.create(13, 3) * Fraction.create(5, 7)
            )
        }
    }

    @Nested
    inner class PowTest {
        @Test
        fun `pow normal`() {
            assertEquals(Fraction.create(9, 4), Fraction.create(3, 2).pow(Flt(2.0)))
        }

        @Test
        fun `pow div`() {
            assertEquals(Flt(2.25), Fraction.create(2, 3).pow(Integer(-2)))
        }
    }

    @Nested
    inner class RemTest {
        @Test
        fun `rem int`() {
            assertEquals(Flt(1.0), Fraction.create(10, 1) % Integer(3))
        }

        @Test
        fun `rem flt`() {
            assertEquals(Flt(0.0), Fraction.create(10, 1) % Flt(2.5))
        }

        @Test
        fun `rem fraction`() {
            assertEquals(Flt(0.0), Fraction.create(10, 1) % Fraction.create(5, 2))
        }
    }
}
