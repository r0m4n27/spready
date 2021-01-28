package spready.lisp.functions.math

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import spready.lisp.BaseEval
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PredicateTest : BaseEval() {
    private fun provider() = Stream.of(
        Pair("(zero? 0/3)", "#t"),
        Pair("(neg? -2.123)", "#t"),
        Pair("(pos? -3/123)", "#f"),
        Pair("(even? 3)", "#f"),
        Pair("(odd? 3)", "#t"),
    )

    @ParameterizedTest
    @MethodSource("provider")
    fun predicates(data: Pair<String, String>) {
        equalsEval(data.second, data.first)
    }
}
