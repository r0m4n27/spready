package spready.lisp.sexpr

import spready.lisp.EvalException
import java.util.Objects

sealed class Num : SExpr, Comparable<Num>

data class Integer(val value: Int) : Num() {
    override fun toString() = value.toString()

    override fun compareTo(other: Num): Int {
        TODO("Not yet implemented")
    }
}

data class Flt(val value: Double) : Num() {
    override fun toString() = value.toString()

    override fun compareTo(other: Num): Int {
        TODO("Not yet implemented")
    }
}

class Fraction private constructor(
    val numerator: Int,
    val denominator: Int
) :
    Num() {

    override fun toString() = "$numerator/$denominator"

    override fun compareTo(other: Num): Int {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Fraction

        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(numerator, denominator)
    }

    companion object {
        fun create(numerator: Int, denominator: Int): Fraction {
            if (denominator == 0) {
                throw EvalException("Denominator can't be zero!")
            }

            if (denominator < 0) {
                throw EvalException("Denominator can't be negative!")
            }

            val gcd = gcd(numerator, denominator)
            val newNumerator = numerator / gcd
            val newDenominator = denominator / gcd

            return Fraction(newNumerator, newDenominator)
        }

        fun gcd(x: Int, y: Int): Int {
            var a = if (x >= 0) x else -x
            var b = if (y >= 0) y else -y

            while (b != 0) {
                b = (a % b).also { a = b }
            }

            return a
        }
    }
}
