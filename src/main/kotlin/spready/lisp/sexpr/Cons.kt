package spready.lisp.sexpr

import spready.lisp.Environment
import spready.lisp.EvalException

data class Cons(val first: SExpr, val second: SExpr) : SExpr(), Iterable<SExpr> {

    override fun eval(env: Environment): SExpr {
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
        return object : Iterator<SExpr> {
            var pointer: SExpr = this@Cons

            override fun hasNext(): Boolean {
                return pointer !is Nil
            }

            override fun next(): SExpr {
                return if (pointer is Cons) {
                    (pointer as Cons).first.also {
                        pointer = (pointer as Cons).second
                    }
                } else {
                    if (pointer is Nil) {
                        throw NoSuchElementException("End of Cons")
                    } else {
                        pointer.also { pointer = Nil }
                    }
                }
            }
        }
    }

    companion object {

        fun List<SExpr>.toCons(): Cons {
            return if (this.isNotEmpty()) {
                buildCons(this.toMutableList()) as Cons
            } else {
                Cons(Nil, Nil)
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
