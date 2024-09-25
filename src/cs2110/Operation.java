package cs2110;

import java.util.Set;
import java.util.HashSet;

/**
 * An expression tree node representing the binary operators common in arithmetic
 */
public class Operation implements Expression {

    private Operator op;
    private Expression left;
    private Expression right;

    /**
     * Creates an Operation node with two operand children, `left` and `right`, and an Operator
     * `op`
     */
    public Operation(Operator op, Expression left, Expression right) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    /**
     * Returns the value of the evaluated values of both of its operand children when they are
     * combined with their operator. This counts as one `operation`.
     */
    @Override
    public double eval(VarTable vars) throws UnboundVariableException {
        return op.operate(left.eval(vars), right.eval(vars));
    }

    /**
     * Return the number of operations and unary functions contained in this expression.
     */
    @Override
    public int opCount() {
        return 1 + left.opCount() + right.opCount();
    }

    /**
     * Returns the infix representation of this expression, in which each Operation is enclosed in
     * parentheses, and its operands are separated from its operator symbol by spaces. Example: What
     * we would write mathematically as (2y + 1)^3 should be expressed as the String "(((2 * y) + 1)
     * ^ 3)".
     */
    @Override
    public String infixString() {
        return "(" + left.infixString() + " " + op.symbol() + " " + right.infixString() + ")";
    }

    /**
     * Returns the postfix representation of this expression, which is denoted by performing
     * postorder traversal. Example: The mathematical expression (2y + 1)^3 should be expressed as
     * the String "2 y * 1 + 3 ^"
     */
    @Override
    public String postfixString() {
        return left.postfixString() + " " + right.postfixString() + " " + op.symbol();
    }

    /**
     * Returns an Expression containing the optimized Operation object. An Operation can be fully
     * optimized to a Constant if both operand children in the Operation object can be fully
     * optimized to Constants. Otherwise, Operation can still be partially optimized by creating a
     * new copy where the children are replaced with their optimized forms.
     */
    @Override
    public Expression optimize(VarTable vars) {
        left = left.optimize(vars);
        right = right.optimize(vars);
        try {
            return new Constant(this.eval(vars));
        } catch (UnboundVariableException e) {
            return new Operation(op, left, right);
        }
    }

    /**
     * Returns a Set containing all the Variable objects that the function depends on.
     */
    @Override
    public Set<String> dependencies() {
        Set<String> deps = new HashSet<String>();
        deps.addAll(left.dependencies());
        deps.addAll(right.dependencies());
        return deps;
    }

    /**
     * Returns true if two Operation nodes are equal, and false if they are not. Two Operation nodes
     * are considered equal if both their operator and operand nodes are equal.
     */
    public boolean equals(Object o) {
        if (o instanceof Operation) {
            Operation var = (Operation) o;
            return op.equals(var.op) && left.equals(var.left) && right.equals(var.right);
        }
        return false;
    }
}
