package cs2110;

import java.util.Set;

/**
 * An expression tree node representing the application of a function to an argument, which can be
 * any non-empty subexpression
 */
public class Application implements Expression {
    private Expression exp;
    private static int count = 0;

    /**
     * Returns the result of applying the function to the argument child passed into the function.
     * Calling its function counts as one `operation`.
     */
    @Override
    public double eval(VarTable vars) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return the number of operations and unary functions contained in this expression.
     */
    @Override
    public int opCount() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the infix representation of this expression, which is denoted by the name of its function,
     * followed by the infix representation of its argument, enclosed in parentheses.
     * Example: sin((y / 2))
     */
    @Override
    public String infixString() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the postfix representation of this expression, which is denoted by the argument's postfix representation,
     * followed by the function name with a "()" suffix.
     * Example: y 2 / sin()
     */
    @Override
    public String postfixString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression optimize(VarTable vars) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> dependencies() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns true if two Application nodes are equal, and false if they are not. Two Application nodes
     * are considered equal if both their functions and arguments are equal
     */
    public boolean equals() {
        throw new UnsupportedOperationException();
    }
}
