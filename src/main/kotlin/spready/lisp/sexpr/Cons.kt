package spready.lisp.sexpr

import spready.lisp.Environment
import spready.lisp.EvalException

data class Cons(val first: SExpr, val second: SExpr) : SExpr(), Iterable<SExpr> {

    override fun eval(env: Environment): SExpr {
        if (first is Unquote) {
            throw EvalException(""", cant be used outside of "`" """)
        }
        if (first is UnquoteSplice) {
            throw EvalException(""",@ cant be used outside of "`" """)
        }

        val firstEvaluated = first.eval(env)
        return if (firstEvaluated is Func) {
            when (second) {
                is Cons -> firstEvaluated(env, second.toList())
                is Nil -> firstEvaluated(env, listOf())
                else -> firstEvaluated(env, listOf(second))
            }
        } else {
            throw EvalException("First element must be a Function not $firstEvaluated")
        }
    }

    override fun toString(): String {
        var pointer: SExpr = this

        return buildString {
            append("(")

            while (pointer is Cons) {
                append("${(pointer as Cons).first}")
                if ((pointer as Cons).second != Nil) {
                    append(" ")
                }
                pointer = (pointer as Cons).second
            }

            // TODO: Support dot notation
            // if (pointer != Nil) {
            //     append(pointer)
            // }

            append(")")
        }
    }

    override fun iterator(): Iterator<SExpr> {
        return iterator {
            var pointer: SExpr = this@Cons
            while (pointer is Cons) {
                yield(pointer.first)
                pointer = pointer.second
            }

            if (pointer !is Nil) {
                yield(pointer)
            }
        }
    }

    companion object {

        fun List<SExpr>.toCons(): SExpr {
            return if (this.isNotEmpty()) {
                buildCons(this.toMutableList()) as Cons
            } else {
                Nil
            }
        }

        private fun buildCons(exprs: MutableList<SExpr>): SExpr {
            return if (exprs.isEmpty()) {
                Nil
            } else {
                Cons(exprs.removeFirst(), buildCons(exprs))
            }
        }
    }
}