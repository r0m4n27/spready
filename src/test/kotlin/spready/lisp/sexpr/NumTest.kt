package spready.lisp.sexpr

import org.junit.jupiter.api.Nested
import spready.lisp.EvalException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NumTest {
    @Nested
    inner class GCDTest {
        @Test
        fun `gcd normal`() {
            assertEquals(4, Num.gcd(4, 20))
        }

        @Test
        fun `gcd negative`() {
            assertEquals(4, Num.gcd(-4, 20))
        }
    }

    @Nested
    inner class LCMTest {
        @Test
        fun `lcm normal`() {
            assertEquals(20, Num.lcm(4, 20))
        }

        @Test
        fun `lcm negative`() {
            assertEquals(20, Num.lcm(-4, 20))
        }
    }

    @Nested
    inner class IntegerTest {
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
    inner class FltTest {
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

        @Test
        fun `compareTo Float`() {
            assertEquals(true, Flt(4.499) < Flt(4.5))
        }

        @Test
        fun `compareTo Fraction`() {
            assertEquals(true, Flt(1.44) < Fraction.create(3, 2))
        }

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
    inner class FractionTest {
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

        @Test
        fun `compareTo Fraction`() {
            assertEquals(
                true,
                Fraction.create(1, 2) < Fraction.create(2, 3)
            )
        }

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
}
