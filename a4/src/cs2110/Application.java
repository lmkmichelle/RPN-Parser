package cs2110;

import java.util.HashSet;
import java.util.Set;

/**
 * An expression tree node representing the application of a function to an argument, which can be
 * any non-empty subexpression
 */
public class Application implements Expression {
    private Expression argument;

    private UnaryFunction func;

    /**
     * Returns the result of applying the function to the argument child passed into the function.
     * Calling its function counts as one `operation`.
     */

    public Application(UnaryFunction func, Expression argument) {
        this.func = func;
        this.argument = argument;
    }
    @Override
    public double eval(VarTable vars) throws UnboundVariableException {
        return func.apply(argument.eval(vars));
    }

    /**
     * Return the number of operations and unary functions contained in this expression.
     */
    @Override
    public int opCount() {
        return 1 + argument.opCount();
    }

    /**
     * Returns the infix representation of this expression, which is denoted by the name of its function,
     * followed by the infix representation of its argument, enclosed in parentheses.
     * Example: sin((y / 2))
     */
    @Override
    public String infixString() {
        return func.name() + "(" + argument.infixString() + ")";
    }

    /**
     * Returns the postfix representation of this expression, which is denoted by the argument's postfix representation,
     * followed by the function name with a "()" suffix.
     * Example: y 2 / sin()
     */
    @Override
    public String postfixString() {
        return argument.postfixString() + " " + func.name() + "()";
    }

    /**
     * Returns an Expression containing the optimized Application object. An Application can be fully optimized to a Constant if
     * the operand children in the Application object can be fully optimized to Constants. Otherwise, Application can still be
     * partially optimized by creating a new copy where the child is replaced with its optimized form.
     */
    @Override
    public Expression optimize(VarTable vars) {
        argument = argument.optimize(vars);
        try {
            return new Constant(this.eval(vars));
        } catch (UnboundVariableException e) {
            return new Application(this.func, argument);
        }
    }

    /**
     * Returns a Set containing all the unique Variable objects used in the operation.
     */
    @Override
    public Set<String> dependencies() {
        return argument.dependencies();
    }

    /**
     * Returns true if two Application nodes are equal, and false if they are not. Two Application nodes
     * are considered equal if both their functions and arguments are equal
     */
    public boolean equals(Object a) {
        if (a instanceof Application) {
            return func.equals(((Application) a).func) && argument.equals(((Application) a).argument);
        }
        return false;
    }
}
