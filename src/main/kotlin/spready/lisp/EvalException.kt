package spready.lisp

/**
 * Will be thrown if something in the evaluation fails
 */
class EvalException(override val message: String) : Throwable(message)
