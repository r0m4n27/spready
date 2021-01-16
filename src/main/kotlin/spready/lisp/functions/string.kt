package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Str
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

object StrLength : Func("string-length") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)

        return Integer(env.eval(args[0]).cast<Str>().value.length)
    }
}

object StrGet : Func("string-get") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)
        val index = env.eval(args[0]).cast<Integer>().value
        val str = env.eval(args[1]).cast<Str>().value

        if (index < 0) {
            throw EvalException("Position cant be negative!")
        }

        return if (index < str.length) {
            Str(str[index].toString())
        } else {
            throw EvalException("String is only ${str.length} big!")
        }
    }
}

object ReplaceChar : Func("replace-char") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(3)
        val index = env.eval(args[0]).cast<Integer>().value
        val newChar = env.eval(args[1]).cast<Str>().value
        val str = env.eval(args[2]).cast<Str>().value

        if (index < 0) {
            throw EvalException("Position cant be negative!")
        }

        if (newChar.length != 1) {
            throw EvalException("ReplacementString can only be 1 long!")
        }

        return if (index < str.length) {
            val chars = str.toCharArray()
            chars[index] = newChar.toCharArray()[0]

            Str(String(chars))
        } else {
            throw EvalException("String is only ${str.length} big!")
        }
    }
}

object SubstringFunc : Func("substring") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)
        val index = env.eval(args[0]).cast<Integer>().value
        val str = env.eval(args[1]).cast<Str>()

        if (index < 0) {
            throw EvalException("Position cant be negative!")
        }

        if (index == 0) {
            return str
        }

        val strLength = str.value.length

        return if (index < strLength) {
            Str(str.value.subSequence(index, strLength).toString())
        } else {
            throw EvalException("String is only $strLength big!")
        }
    }
}

object AppendStr : Func("string-append") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkMinSize(1)

        val resultStr = args.map {
            env.eval(it).cast<Str>()
        }.fold("") { acc, str ->
            acc + str.value
        }

        return Str(resultStr)
    }
}

object StrFill : Func("string-fill") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)

        val str = env.eval(args[1]).cast<Str>()
        val strLength = str.value.length

        val newChar = env.eval(args[0]).cast<Str>().value

        if (newChar.length != 1) {
            throw EvalException("New Char can only be 1 long!")
        }

        return Str(newChar.repeat(strLength))
    }
}

fun stringFunctions(): List<Pair<Symbol, Func>> {
    return listOf(
        StrLength,
        StrGet,
        SubstringFunc,
        AppendStr,
        StrFill,
        ReplaceChar
    ).map {
        Pair(Symbol(it.name), it)
    }
}
