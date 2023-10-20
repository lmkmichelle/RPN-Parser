package cs2110;

import java.util.Set;

/**
 * An expression tree node representing a named variable (like y) in an expression
 */
public class Variable implements Expression {

    private static int count = 0;
    String name;

    public Variable(String name) {
        this.name = name;
    }

    /**
     * Return the value of a Variable object if it exists in VarTable `vars`.
     * Throws UnboundVariableExpression if this expression contains a variable whose
     * value is not in `vars`.
     * A call to this function does not count as an `operation`.
     */
    @Override
    public double eval(VarTable vars) throws UnboundVariableException {
        return vars.get(name);
    }

    /**
     * Return the number of operations and unary functions contained in this expression.
     * Does not count calls to `eval()`.
     */
    @Override
    public int opCount() {
        return count;
    }

    /**
    * Return the infix representation of this Variable, which is simply represented by its name,
    * regardless of notation.
    * Example: "y", "x"
    */
    @Override
    public String infixString() {
        return name;
    }

    /**
     * Return the postfix representation of this Variable, which is simply represented by its name,
     * regardless of notation.
     * Example: "y", "x"
     */
    @Override
    public String postfixString() {
        return name;
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
     * Returns true if two variables are equal, and false if they are not. Variables are considered equal if they
     * represent the same variable name.
     */
    public boolean equals(Variable vars) {
        return name == vars.name;
    }
}
