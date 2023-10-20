package cs2110;

import java.util.Set;

/**
 * An expression tree node representing the binary operators common in arithmetic
 */
public class Operation implements Expression {

    private static int count = 0;
    private Operator o;
    private Constant a;
    private Constant b;

    public Operation(Operator o, Constant a, Constant b){
        this.a = a;
        this.b = b;
        this.o = o;
    }

    /**
     * Returns the value of the evaluated values of both of its operand children when they are combined
     * with their operator.
     * This counts as one `operation`.
     */
    @Override
    public double eval(VarTable vars) {
        count += 1;
        return o.operate(a.value, b.value);
    }

    @Override
    public int opCount() {
        return count;
    }

    /**
     * Returns the infix representation of this expression, in which each Operation is enclosed in parentheses,
     * and its operands are separated from its operator symbol by spaces.
     * Example: What we would write mathematically as (2y + 1)^3 should be expressed as the String
     * "(((2 * y) + 1) ^ 3)".
     */
    @Override
    public String infixString() {
        String f = "";
        f += "(" + a.value + " " + o.toString() + " " + b.value + ")";
        return f;
    }

    /**
     * Returns the postfix representation of this expression, which is denoted by performing postorder traversal.
     * Example: The mathematical expression (2y + 1)^3 should be expressed as the String "2 y * 1 + 3 ^"
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
     * Returns true if two Operation nodes are equal, and false if they are not. Two Operation nodes
     * are considered equal if both their operator and operand nodes are equal.
     */
    public boolean equals() {
        throw new UnsupportedOperationException();
    }
}
