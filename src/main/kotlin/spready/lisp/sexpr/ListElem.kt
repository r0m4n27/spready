package spready.lisp.sexpr

import spready.lisp.Environment
import spready.lisp.EvalException

/**
 * Defines a node if a list
 *
 * Lists in lisp are implemented as a linked list
 *
 * Can be iterated over
 */
sealed class ListElem : SExpr, Iterable<SExpr> {
    abstract val head: SExpr
    abstract val tail: SExpr

    companion object {

        /**
         * Converts a kotlin list to a [SExpr] list
         */
        fun List<SExpr>.toListElem(): ListElem {
            return this.foldRight<SExpr, ListElem>(Nil) { head, tail ->
                Cons(head, tail)
            }
        }

        /**
         * Converts a kotlin list to a [SExpr] list without a [Nil] at the end
         */
        fun List<SExpr>.toConsWithTail(): SExpr {
            if (this.size <= 1) {
                throw IllegalArgumentException("Cant convert to Cons with tail!")
            }

            return this.reduceRight { expr, acc ->
                Cons(expr, acc)
            }
        }
    }
}

/**
 * Represents the node in the linked list
 *
 * But doesn't have to hold another [ListElem] a it's tail
 */
data class Cons(override val head: SExpr, override val tail: SExpr) : ListElem() {

    /**
     *  Evaluates the list
     *
     *  Evaluates the first and if it's a function it invokes it with the tail
     *
     * @throws EvalException if the head is [Unquote] or [UnquoteSplice]
     * @throws EvalException if the evaluated first element isn't a [Func]
     */
    override fun eval(env: Environment): SExpr {
        if (head is Unquote) {
            throw EvalException(""", cant be used outside of "`"""")
        }
        if (head is UnquoteSplice) {
            throw EvalException(""",@ cant be used outside of "`"""")
        }

        val firstEvaluated = head.eval(env)
        return if (firstEvaluated is Func) {
            when (tail) {
                is Cons -> firstEvaluated(env, tail.toList())
                is Nil -> firstEvaluated(env, listOf())
                else -> firstEvaluated(env, listOf(tail))
            }
        } else {
            throw EvalException("First element must be a Function not $firstEvaluated")
        }
    }

    override fun toString(): String {

        return buildString {
            append("(")
            var pointer: SExpr = this@Cons

            while (pointer is Cons) {
                append(pointer.head)

                if (pointer.tail !is Nil) {
                    append(" ")
                }

                pointer = pointer.tail
            }

            if (pointer !is Nil) {
                append(". $pointer")
            }

            append(")")
        }
    }

    /**
     * Iterates over the list until the tail isn't Cons
     */
    override fun iterator(): Iterator<SExpr> {
        return iterator {
            var pointer: SExpr = this@Cons
            while (pointer is Cons) {
                yield(pointer.head)
                pointer = pointer.tail
            }

            if (pointer !is Nil) {
                yield(pointer)
            }
        }
    }
}

/**
 * Represents a empty list or the end of it
 */
object Nil : ListElem() {
    override fun toString() = "nil"
    override fun iterator(): Iterator<SExpr> = iterator { }

    override val head: SExpr = this
    override val tail: SExpr = this

    /**
     * Nil represents false in lisp
     */
    override fun toBool(): Bool = Bool(false)
}
