package spready

import spready.lisp.Environment
import spready.lisp.parse
import spready.lisp.tokenize

fun main() {
    val test =
        """
            (let ((x 2)) x)
        """.trimIndent()
    val tokens = tokenize(test)
    val parsed = parse(tokens)
    val env = Environment.defaultEnv()
    val evaluated = parsed.map { env.eval(it) }
    evaluated.forEach {
        println(it)
    }
}
