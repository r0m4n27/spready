package spready.lisp.sexpr

import spready.lisp.EvalException
import kotlin.math.abs
import kotlin.math.ceil

sealed class Num : SExpr, Comparable<Num> {
    abstract fun toFlt(): Flt
    abstract fun toInteger(): Integer
    abstract fun toFraction(): Fraction

    companion object {
        fun gcd(x: Int, y: Int): Int {
            var a = if (x >= 0) x else -x
            var b = if (y >= 0) y else -y

            while (b != 0) {
                b = (a % b).also { a = b }
            }

            return a
        }

        fun lcm(x: Int, y: Int): Int {
            return abs(x * y) / gcd(x, y)
        }
    }
}

class Integer(val value: Int) : Num() {
    override fun toString() = value.toString()

    override fun compareTo(other: Num): Int {
        return when (other) {
            is Integer -> value.compareTo(other.value)
            is Flt -> value.compareTo(other.value)
            is Fraction -> (value * other.denominator).compareTo(other.numerator)
        }
    }

    override fun toFlt() = Flt(value.toDouble())

    override fun toInteger() = this

    override fun toFraction() = Fraction.create(value, 1)

    override fun equals(other: Any?): Boolean {
        if (other !is Num) {
            return false
        }

        return compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

class Flt(val value: Double) : Num() {
    override fun toString() = value.toString()

    override fun compareTo(other: Num): Int {
        return when (other) {
            is Integer -> -other.compareTo(this)
            is Flt -> value.compareTo(other.value)
            is Fraction -> value.compareTo(other.toFlt().value)
        }
    }

    override fun toFlt() = this

    override fun toInteger() = Integer(value.toInt())

    override fun toFraction(): Fraction {
        return if (value == ceil(value)) {
            Fraction.create(value.toInt(), 1)
        } else {
            throw EvalException("Cant convert float to a fraction!")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Num) {
            return false
        }

        return compareTo(other) == 0
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

class Fraction private constructor(
    val numerator: Int,
    val denominator: Int
) :
    Num() {

    override fun toString() = "$numerator/$denominator"

    override fun compareTo(other: Num): Int {
        return when (other) {
            is Integer -> -other.compareTo(this)
            is Flt -> -other.compareTo(this)
            is Fraction -> {
                val lcm = lcm(denominator, other.denominator)

                (numerator * lcm).compareTo(other.numerator * lcm)
            }
        }
    }

    override fun toFlt() = Flt(numerator.toDouble() / denominator)

    override fun toInteger(): Integer {
        return Integer(numerator / denominator)
    }

    override fun toFraction() = this

    override fun equals(other: Any?): Boolean {
        if (other !is Num) {
            return false
        }

        return when (other) {
            is Integer, is Flt -> compareTo(other) == 0
            is Fraction ->
                denominator == other.denominator &&
                    numerator == other.numerator
        }
    }

    override fun hashCode(): Int {
        return 31 * numerator + denominator
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
    }
}
