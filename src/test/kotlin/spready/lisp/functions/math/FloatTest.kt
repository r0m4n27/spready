package spready.lisp.functions.math

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import spready.lisp.BaseEval
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FloatTest : BaseEval() {
    private fun provider() = Stream.of(
        Pair("(truncate (sin #pi))", "0"),
        Pair("(cos #pi)", "-1"),
        Pair("(truncate (tan #pi))", "0"),
        Pair("(asin 0.0)", "0"),
        Pair("(acos 1.0)", "0"),
        Pair("(atan 0.0)", "0"),
        Pair("(exp 1)", "#e"),
        Pair("(log #e)", "1"),
        Pair("(log 100 10)", "2")
    )

    @ParameterizedTest
    @MethodSource("provider")
    fun functions(data: Pair<String, String>) {
        equalsEval(data.second, data.first)
    }
}
