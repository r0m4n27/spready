package spready.lisp.sexpr.num

import org.junit.jupiter.api.Nested
import spready.lisp.EvalException
import spready.lisp.sexpr.Flt
import spready.lisp.sexpr.Fraction
import spready.lisp.sexpr.Integer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FltTest {
    @Test
    fun `unary minus`() {
        assertEquals(Flt(-10.0), -Flt(10.0))
    }

    @Test
    fun abs() {
        assertEquals(Flt(3.0), Flt(3.0))
    }

    @Nested
    inner class Conversion {
        @Test
        fun `toFlt normal`() {
            assertEquals(123.123, Flt(123.123).toFlt().value)
        }

        @Test
        fun `toInteger normal`() {
            assertEquals(123, Flt(123.678).toInteger().value)
        }

        @Test
        fun `toFraction normal`() {
            val fraction = Flt(123.0000).toFraction()

            assertEquals(123, fraction.numerator)
            assertEquals(1, fraction.denominator)
        }

        @Test
        fun `toFraction fail`() {
            assertFailsWith<EvalException> {
                Flt(123.123).toFraction()
            }
        }
    }

    @Nested
    inner class Compare {
        @Test
        fun `compareTo Float`() {
            assertEquals(true, Flt(4.499) < Flt(4.5))
        }

        @Test
        fun `compareTo Fraction`() {
            assertEquals(true, Flt(1.44) < Fraction.create(3, 2))
        }
    }

    @Nested
    inner class Equals {
        @Test
        fun `equals Float`() {
            assertEquals(true, Flt(4.000) == Flt(4.0))
        }

        @Test
        fun `equals Fraction`() {
            assertEquals(true, Flt(4.000).equals(Fraction.create(8, 2)))
        }

        @Test
        fun `equals Any`() {
            assertEquals(false, Flt(1.0) == ("Test" as Any))
        }

        @Test
        fun `equals null`() {
            assertEquals(false, Flt(1.0).equals(null))
        }
    }

    @Nested
    inner class Plus {
        @Test
        fun `plus Float`() {
            assertEquals(Flt(10.0), Flt(3.0) + Flt(7.0))
        }

        @Test
        fun `plus Fraction`() {
            assertEquals(
                Flt(4.5),
                Flt(3.0) + Fraction.create(3, 2)
            )
        }
    }

    @Nested
    inner class Times {
        @Test
        fun `times Float`() {
            assertEquals(Flt(21.0), Flt(3.0) * Flt(7.0))
        }

        @Test
        fun `times Fraction`() {
            assertEquals(
                Flt(4.5),
                Flt(3.0) * Fraction.create(3, 2)
            )
        }
    }

    @Nested
    inner class InvertTest {
        @Test
        fun invert() {
            assertEquals(Flt(0.5), Flt(2.0).invert())
        }

        @Test
        fun `invert NaN`() {
            assertEquals(Flt(Double.POSITIVE_INFINITY), Flt(0.0).invert())
        }
    }

    @Nested
    inner class PowTest {
        @Test
        fun `pow normal`() {
            assertEquals(Flt(9.0), Flt(3.0).pow(Integer(2)))
        }

        @Test
        fun `pow fraction`() {
            assertEquals(Flt(8.0), Flt(4.0).pow(Fraction.create(3, 2)))
        }
    }

    @Nested
    inner class RemTest {
        @Test
        fun `rem int`() {
            assertEquals(Flt(1.0), Flt(10.0) % Integer(3))
        }

        @Test
        fun `rem flt`() {
            assertEquals(Flt(0.0), Flt(10.0) % Flt(2.5))
        }

        @Test
        fun `rem fraction`() {
            assertEquals(Flt(0.0), Flt(10.0) % Fraction.create(5, 2))
        }
    }
}
