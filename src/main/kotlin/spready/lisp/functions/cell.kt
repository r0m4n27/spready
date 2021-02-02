package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.ListElem.Companion.toListElem
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

object CellRange : Func("cell-range") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)

        val evaluated = env.eval(args).cast<Cell>()

        return env.eval(evaluated[0]..evaluated[1]).toListElem()
    }
}

fun cellFunctions(): List<Pair<Symbol, Func>> =
    listOf(CellRange).map {
        Pair(Symbol(it.name), it)
    }
