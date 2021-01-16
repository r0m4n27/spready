package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.ListElem
import spready.lisp.sexpr.ListElem.Companion.toListElem
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

object ListFunc : Func("list") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        return env.eval(args).toListElem()
    }
}

object ConsFunc : Func("cons") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)

        return Cons(env.eval(args[0]), env.eval(args[1]))
    }
}

object Head : Func("head") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)

        val evaluated = env.eval(args[0]).cast<ListElem>()
        return evaluated.head
    }
}

object SetHead : Func("set-head") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)
        val cons = env.eval(args[0]).cast<Cons>()
        val newVal = env.eval(args[1])

        return Cons(newVal, cons.tail)
    }
}

object Tail : Func("tail") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)

        val evaluated = env.eval(args[0]).cast<ListElem>()
        return evaluated.tail
    }
}

object SetTail : Func("set-tail") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)
        val cons = env.eval(args[0]).cast<Cons>()
        val newVal = env.eval(args[1])

        return Cons(cons.head, newVal)
    }
}

object ConsLength : Func("len") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)

        val evaluated = env.eval(args[0]).cast<ListElem>()
        return Integer(evaluated.toList().size)
    }
}

object AppendCons : Func("append") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val rest = args.toMutableList().checkMinSize(1)

        val start = env.eval(rest.removeFirst()).cast<ListElem>().toMutableList()

        rest.map {
            env.eval(it)
        }.forEach {
            if (it is ListElem) {
                it.forEach { elem ->
                    start.add(elem)
                }
            } else {
                start.add(it)
            }
        }

        return start.toListElem()
    }
}

object ReverseCons : Func("reverse") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)
        val eval = env.eval(args[0]).cast<ListElem>()

        return eval.toList().reversed().toListElem()
    }
}

object GetCons : Func("get") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)
        val consList = env.eval(args[1]).cast<ListElem>().toList()
        val pos = env.eval(args[0]).cast<Integer>()

        if (pos.value < 0) {
            throw EvalException("Position cant be negative!")
        }

        return if (pos.value < consList.size) {
            consList[pos.value]
        } else {
            throw EvalException("List is only ${consList.size} big!")
        }
    }
}

object Sublist : Func("sublist") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)
        val evaluated = env.eval(args[1])
        val pos = env.eval(args[0]).cast<Integer>()

        if (pos.value < 0) {
            throw EvalException("Position cant be negative!")
        }

        if (pos.value == 0) {
            return evaluated
        }

        val listElem = evaluated.cast<ListElem>()
        val consList = listElem.toList()

        return if (pos.value < consList.size) {
            consList.subList(pos.value, consList.size).toListElem()
        } else {
            throw EvalException("List is only ${consList.size} big!")
        }
    }
}

object Member : Func("member") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val mutArgs = args.toMutableList().checkBetweenSize(2, 3)
        val search = env.eval(mutArgs.removeFirst())
        val cons = env.eval(mutArgs.removeFirst()).cast<ListElem>()

        val eqFun = if (mutArgs.isEmpty()) {
            EqExpr
        } else {
            env.eval(mutArgs.first()).cast<Func>()
        }

        if (cons is Nil) {
            return Nil
        }

        var pointer: SExpr = cons

        while (pointer is Cons) {
            val evaluated = eqFun(env, listOf(search, pointer.head))

            if (evaluated.toBool().value) {
                return pointer
            } else {
                pointer = pointer.tail
            }
        }

        return Nil
    }
}

object Assoc : Func("assoc") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val mutArgs = args.toMutableList().checkBetweenSize(2, 3)
        val search = env.eval(mutArgs.removeFirst())
        val cons = env.eval(mutArgs.removeFirst()).cast<ListElem>()

        val eqFun = if (mutArgs.isEmpty()) {
            EqExpr
        } else {
            env.eval(mutArgs.first()).cast<Func>()
        }

        cons.map {
            it.cast<ListElem>()
        }.forEach {
            val evaluated = eqFun(env, listOf(search, it.head))

            if (evaluated.toBool().value) {
                return it
            }
        }

        return Nil
    }
}

fun listFunctions(): List<Pair<Symbol, Func>> {
    return listOf(
        ListFunc,
        ConsFunc,
        Head,
        Tail,
        SetHead,
        SetTail,
        ConsLength,
        AppendCons,
        ReverseCons,
        GetCons,
        Sublist,
        Member,
        Assoc
    ).map {
        Pair(Symbol(it.name), it)
    }
}
