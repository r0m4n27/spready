package spready.lisp.functions.math

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import spready.lisp.BaseEval
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApproxTest : BaseEval() {

    private fun approxProvider() = Stream.of(
        Triple("ceil", "3", "3"),
        Triple("floor", "3", "3"),
        Triple("truncate", "3", "3"),
        Triple("round", "3", "3"),

        Triple("ceil", "3.1", "4.0"),
        Triple("floor", "3.5", "3.0"),
        Triple("truncate", "3.123", "3.0"),
        Triple("round", "3.6", "4.0"),
    )

    @ParameterizedTest
    @MethodSource("approxProvider")
    fun approx(data: Triple<String, String, String>) {
        equalsEval(data.third, "(${data.first} ${data.second})")
    }
}
