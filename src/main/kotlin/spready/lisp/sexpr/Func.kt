package spready.lisp.sexpr

import spready.lisp.Environment
import spready.lisp.EvalException

abstract class Func(val name: String) : SExpr() {

    // TODO: Better representation
    override fun toString() = "Function #$name"

    abstract operator fun invoke(env: Environment, args: List<SExpr>): SExpr

    protected fun <T : List<SExpr>> T.checkSize(size: Int): T {
        if (this.size != size) {
            throw EvalException("Can only have $size arguments not ${this.size}")
        }

        return this
    }

    protected fun <T : List<SExpr>> T.checkMinSize(size: Int): T {
        if (this.size < size) {
            throw EvalException(
                "Must have at least $size arguments not ${this.size}"
            )
        }

        return this
    }
}
