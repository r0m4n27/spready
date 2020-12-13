package spready

import spready.lisp.parse
import spready.lisp.tokenize

fun main() {
    val test = "(123 ()"
    val tokens = tokenize(test)
    val expr = parse(tokens)
    print(expr)
}
