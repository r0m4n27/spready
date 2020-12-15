package spready.lisp

fun evalAll(cons: Cons, environment: Environment): List<SExpr> {
    return cons.map {
        it.eval(environment)
    }
}
