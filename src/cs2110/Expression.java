package cs2110;

import java.util.Set;

/**
 * Represents an arithmetic expression capable of being evaluated to yield a floating-point number.
 * The expression may include constants, variables, binary operations, and unary function
 * evaluations.
 */
public interface Expression {

    /**
     * Return the result of evaluating this expression, substituting any variables with their value
     * in `vars`.  Throws UnboundVariableExpression if this expression contains a variable whose
     * value is not in `vars`.
     */
    double eval(VarTable vars) throws UnboundVariableException;

    /**
     * Return the number of operations and unary functions contained in this expression.
     */
    int opCount();

    /**
     * Return the infix representation of this expression, enclosing every binary operation in
     * parentheses (regardless of whether they are necessary to preserve the order of operations).
     * Example: "(((1.5 * 2.0) + 3.5) / cos(-4.25))"
     */
    String infixString();

    /**
     * Return the postfix representation of this string, separating every token with spaces.
     * Example: "1.5 2.0 * 3.5 + -4.25 cos() /"
     */
    String postfixString();

    /**
     * Return an expression where all operations and function evaluations that only depend on
     * constants or variables in `vars` are replaced by constants equal to their evaluated value.
     * The returned expression will evaluate to the same values as this one for any variable map
     * that is a superset of `vars`.
     */
    Expression optimize(VarTable vars);

    /**
     * Return the names of all variables that this expression depends on.  The returned set need not
     * be modifiable.
     */
    Set<String> dependencies();

}
