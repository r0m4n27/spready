package spready.lisp.sexpr

import spready.lisp.Environment
import spready.lisp.EvalException

/**
 * A function that can be implemented to do something with the args
 */
abstract class Func(val name: String) : SExpr {

    override fun toString() = "#<$name>"

    /**
     * The Func can be invoked with a env and a list of passed args
     */
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

    protected fun <T : List<SExpr>> T.checkBetweenSize(min: Int, max: Int): T {
        if (this.size < min || this.size > max) {
            throw EvalException(
                "Args must be between $min and $max not ${this.size}!"
            )
        }

        return this
    }
}
