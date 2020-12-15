package spready.lisp

sealed class SExpr {
    abstract fun eval(env: Environment): SExpr
}

data class Symbol(val value: String) : SExpr() {
    override fun eval(env: Environment): SExpr {
        return env[this]
    }

    override fun toString() = value
}

data class Str(val value: String) : SExpr() {
    override fun eval(env: Environment): SExpr = this

    override fun toString() = "\"$value\""
}

data class Num(val value: Int) : SExpr() {
    override fun toString() = value.toString()

    override fun eval(env: Environment): SExpr = this
}

object Nil : SExpr() {
    override fun toString() = "nil"
    override fun eval(env: Environment): SExpr = this
}

data class Cons(val first: SExpr, val second: SExpr) : SExpr(), Iterable<SExpr> {

    override fun eval(env: Environment): SExpr {
        val firstEvaluated = first.eval(env)
        return if (firstEvaluated is Func) {
            if (second is Cons) {
                firstEvaluated(env, second)
            } else {
                firstEvaluated(env, Cons(second, Nil))
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
                    (pointer as Cons).first.also { pointer = (pointer as Cons).second }
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
}

abstract class Func(val name: String) : SExpr() {

    override fun eval(env: Environment): SExpr = this

    override fun toString() = "Function #$name"

    abstract operator fun invoke(env: Environment, args: Cons): SExpr
}
