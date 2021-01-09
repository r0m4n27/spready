package spready.lisp

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

sealed class SExpr {
    open fun eval(env: Environment): SExpr = this
    open fun toBool(): Bool = Bool(true)

    fun <T : SExpr> cast(type: KClass<T>): T {
        return type.safeCast(this)
            ?: throw EvalException("Expected ${type.simpleName} got $this!")
    }
}

data class Symbol(val value: String) : SExpr() {
    override fun eval(env: Environment): SExpr {
        return env[this]
    }

    override fun toString() = value
}

data class Str(val value: String) : SExpr() {
    override fun toString() = "\"$value\""
}

data class Num(val value: Int) : SExpr() {
    override fun toString() = value.toString()
}

data class Bool(val value: Boolean) : SExpr() {
    override fun toString() = value.toString()
    override fun toBool(): Bool = this
}

object Nil : SExpr() {
    override fun toString() = "nil"
    override fun toBool(): Bool = Bool(false)
}

data class Cons(val first: SExpr, val second: SExpr) : SExpr(), Iterable<SExpr> {

    override fun eval(env: Environment): SExpr {
        val firstEvaluated = first.eval(env)
        return if (firstEvaluated is Func) {
            if (second is Cons) {
                firstEvaluated(env, second.toList())
            } else {
                firstEvaluated(env, listOf(second))
            }
        } else {
            throw EvalException("First element must be a Function not $firstEvaluated")
        }
    }

    fun evalAll(env: Environment): List<SExpr> {
        return this.map {
            it.eval(env)
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

abstract class Func(val name: String) : SExpr() {

    override fun toString() = "Function #$name"

    abstract operator fun invoke(env: Environment, args: List<SExpr>): SExpr

    protected fun List<SExpr>.checkSize(size: Int): List<SExpr> {
        if (this.size != size) {
            throw EvalException("Can only have $size arguments not ${this.size}")
        }

        return this
    }

    protected fun List<SExpr>.checkMinSize(size: Int): List<SExpr> {
        if (this.size < size) {
            throw EvalException(
                "Must have at least $size arguments not ${this.size}"
            )
        }

        return this
    }
}
