package spready.lisp

sealed class SExpr

data class Symbol(val name: String) : SExpr() {
    override fun toString() = name
}

data class Str(val name: String) : SExpr() {
    override fun toString() = name
}

data class Num(val num: Int) : SExpr() {
    override fun toString() = num.toString()
}

object Nil : SExpr() {
    override fun toString() = "nil"
}

data class Cons(val first: SExpr, val second: SExpr) : SExpr() {

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

            if (pointer != Nil) {
                append(pointer)
            }

            append(")")
        }
    }
}
