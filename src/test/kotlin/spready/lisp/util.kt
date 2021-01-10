package spready.lisp

import spready.lisp.sexpr.SExpr

fun evalString(string: String, env: Environment): SExpr {
    return env.eval(parse(tokenize(string)).first())
}
