package spready.lisp.sexpr

import spready.lisp.Environment
import spready.lisp.EvalException

sealed class ListElem : SExpr, Iterable<SExpr> {
    abstract val head: SExpr
    abstract val tail: SExpr

    companion object {
        fun List<SExpr>.toListElem(): ListElem {
            return this.foldRight<SExpr, ListElem>(Nil) { head, tail ->
                Cons(head, tail)
            }
        }

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

data class Cons(override val head: SExpr, override val tail: SExpr) : ListElem() {

    override fun eval(env: Environment): SExpr {
        if (head is Unquote) {
            throw EvalException(""", cant be used outside of "`" """)
        }
        if (head is UnquoteSplice) {
            throw EvalException(""",@ cant be used outside of "`" """)
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

object Nil : ListElem() {
    override fun toString() = "nil"
    override fun iterator(): Iterator<SExpr> = iterator { }

    override val head: SExpr = this
    override val tail: SExpr = this

    override fun toBool(): Bool = Bool(false)
}
