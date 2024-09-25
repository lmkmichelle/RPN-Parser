package cs2110;

import java.util.HashSet;
import java.util.Set;

/**
 * An expression tree node representing a named variable (like y) in an expression
 */
public class Variable implements Expression {

    private static int count = 0;
    String name;

    /**
     * Creates a variable node with the name `name`.
     */
    public Variable(String name) {
        this.name = name;
    }

    /**
     * Return the value of a Variable object if it exists in VarTable `vars`. Throws
     * UnboundVariableExpression if this expression contains a variable whose value is not in
     * `vars`. A call to this function does not count as an `operation`.
     */
    @Override
    public double eval(VarTable vars) throws UnboundVariableException {
        return vars.get(name);
    }

    /**
     * Return the number of operations and unary functions contained in this expression. Does not
     * count calls to `eval()`.
     */
    @Override
    public int opCount() {
        return count;
    }

    /**
     * Return the infix representation of this Variable, which is simply represented by its name,
     * regardless of notation. Example: "y", "x"
     */
    @Override
    public String infixString() {
        return name;
    }

    /**
     * Return the postfix representation of this Variable, which is simply represented by its name,
     * regardless of notation. Example: "y", "x"
     */
    @Override
    public String postfixString() {
        return name;
    }

    /**
     * Returns an Expression containing the optimized Variable object. If the Variable object has an
     * assigned value within the provided Variable Table, then optimize() will return a Constant
     * object representing that value. Otherwise, it will optimize to itself.
     */
    @Override
    public Expression optimize(VarTable vars) {
        try {
            return new Constant(vars.get(name));
        } catch (UnboundVariableException e) {
            return this;
        }
    }


    /**
     * Returns a Set containing all the Variable objects that a formula depends on. A Variable node
     * will depend only on itself.
     */
    @Override
    public Set<String> dependencies() {
        Set<String> deps = new HashSet<String>();
        deps.add(name);
        return deps;
    }

    /**
     * Returns true if two variables are equal, and false if they are not. Variables are considered
     * equal if they represent the same variable name.
     */

    @Override
    public boolean equals(Object vars) {
        if (vars instanceof Variable) {
            return name.equals(((Variable) vars).name);
        }
        return false;
    }
}
