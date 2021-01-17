package spready.lisp.sexpr.num

import org.junit.jupiter.api.Nested
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Num
import kotlin.test.Test
import kotlin.test.assertEquals

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

    @Test
    fun `minus normal`() {
        assertEquals(Integer(12), Integer(15) - Integer(3))
    }

    @Test
    fun `div normal`() {
        assertEquals(Integer(5), Integer(15) / Integer(3))
    }
}
