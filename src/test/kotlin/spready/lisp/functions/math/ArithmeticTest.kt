package spready.lisp.functions.math

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import spready.lisp.BaseEval
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArithmeticTest : BaseEval() {

    private fun provider() = Stream.of(
        Pair("(+ 1 2 3)", "6"),
        Pair("(- 10 2 3)", "5"),
        Pair("(* 3 4 2)", "24"),
        Pair("(* 3 0 2)", "0"),
        Pair("(/ 12 3 2)", "2"),
        Pair("(negate -2)", "2"),
        Pair("(invert 3/2)", "2/3"),
        Pair("(abs 3)", "3"),
        Pair("(pow 3 2)", "9"),
        Pair("(gcd 12 4)", "4"),
        Pair("(lcm 12 4)", "12"),
        Pair("(quotient -10 3)", "-3"),
        Pair("(modulo -10 3)", "-1")
    )

    @ParameterizedTest
    @MethodSource("provider")
    fun arithmetic(data: Pair<String, String>) {
        equalsEval(data.second, data.first)
    }
}
