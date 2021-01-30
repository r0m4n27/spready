package spready.lisp.sexpr

import spready.lisp.EvalException
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow

sealed class Num : SExpr, Comparable<Num> {
    abstract fun toFlt(): Flt
    abstract fun toInteger(): Integer
    abstract fun toFraction(): Fraction

    abstract operator fun plus(other: Num): Num
    abstract operator fun times(other: Num): Num

    abstract operator fun unaryMinus(): Num
    abstract fun invert(): Num
    abstract fun abs(): Num

    abstract fun pow(other: Num): Num
    abstract operator fun rem(other: Num): Num

    operator fun minus(other: Num): Num {
        return this + (-other)
    }

    operator fun div(other: Num): Num {
        return this * other.invert()
    }

    protected fun powOfVal(x: Int, other: Num): Double {
        return when (other) {
            is Integer -> x.toDouble().pow(other.value)
            is Flt -> x.toDouble().pow(other.value)
            is Fraction -> {
                val pow = x.toDouble().pow(other.numerator)
                pow.pow(1.0 / other.denominator)
            }
        }
    }

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

    override fun plus(other: Num): Num {
        return when (other) {
            is Integer -> Integer(value + other.value)
            is Flt -> Flt(value.toDouble() + other.value)
            is Fraction -> Fraction.create(
                (value * other.denominator) + other.numerator,
                other.denominator
            )
        }
    }

    override fun times(other: Num): Num {
        return when (other) {
            is Integer -> Integer(value * other.value)
            is Flt -> Flt(value.toDouble() * other.value)
            is Fraction -> Fraction.create(
                value * other.numerator,
                other.denominator
            )
        }
    }

    override fun unaryMinus() = Integer(-value)
    override fun invert(): Num {
        if (value == 0) {
            throw EvalException("Cant divide by zero!")
        }

        return Fraction.create(1, value)
    }

    override fun abs() = Integer(abs(value))
    override fun pow(other: Num): Num {
        val power = powOfVal(value, other)

        return if (power == ceil(power)) {
            Integer(power.toInt())
        } else {
            Flt(power)
        }
    }

    override fun rem(other: Num): Num {
        return when (other) {
            is Integer -> Integer(value % other.value)
            is Flt, is Fraction -> Flt(value.toDouble() % other.toFlt().value)
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Num) {
            return false
        } else {
            compareTo(other) == 0
        }
    }

    override fun hashCode() = value.hashCode()
}

class Flt(val value: Double) : Num() {
    override fun toString(): String {
        return when (value) {
            E -> "#e"
            PI -> "#pi"
            else -> value.toString()
        }
    }

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

    override fun plus(other: Num): Num {
        return when (other) {
            is Integer -> other + this
            is Flt -> Flt(value + other.value)
            is Fraction -> Flt(value + other.toFlt().value)
        }
    }

    override fun times(other: Num): Num {
        return when (other) {
            is Integer -> other * this
            is Flt -> Flt(value * other.value)
            is Fraction -> Flt(value * other.toFlt().value)
        }
    }

    override fun unaryMinus() = Flt(-value)
    override fun invert() = Flt(1.0 / value)

    override fun abs() = Flt(abs(value))
    override fun pow(other: Num) = Flt(value.pow(other.toFlt().value))
    override fun rem(other: Num) = Flt(value % other.toFlt().value)

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
                val extendedNum = (numerator * (lcm / denominator))
                val otherExtendedNum = other.numerator * (lcm / other.denominator)

                extendedNum.compareTo(otherExtendedNum)
            }
        }
    }

    override fun toFlt() = Flt(numerator.toDouble() / denominator)

    override fun toInteger(): Integer {
        return Integer(numerator / denominator)
    }

    override fun toFraction() = this

    override fun plus(other: Num): Num {
        return when (other) {
            is Integer -> other + this
            is Flt -> other + this
            is Fraction -> {
                val lcm = lcm(denominator, other.denominator)
                val extendedNum = numerator * (lcm / denominator)
                val extendedNumOther = other.numerator * (lcm / other.denominator)

                create(extendedNum + extendedNumOther, lcm)
            }
        }
    }

    override fun times(other: Num): Num {
        return when (other) {
            is Integer -> other * this
            is Flt -> other * this
            is Fraction -> create(
                numerator * other.numerator,
                denominator * other.denominator
            )
        }
    }

    override fun unaryMinus() = create(-numerator, denominator)
    override fun invert(): Num = create(denominator, numerator)
    override fun abs() = create(abs(numerator), denominator)
    override fun pow(other: Num): Num {
        val powNum = powOfVal(numerator, other)
        val powDenominator = powOfVal(denominator, other)

        return if (powNum == ceil(powNum) && powDenominator == ceil(powDenominator)) {
            create(powNum.toInt(), powDenominator.toInt())
        } else {
            Flt(powNum / powDenominator)
        }
    }

    override fun rem(other: Num) = Flt(toFlt().value % other.toFlt().value)

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
