package cs2110;

import java.util.Set;

/**
 * An expression tree node representing a fixed real number.
 */
public class Constant implements Expression {

    /**
     * The value of this expression.
     */
    final double value;

    /**
     * Create a node representing the value `value`.
     */
    public Constant(double value) {
        this.value = value;
    }

    /**
     * Return this node's value.
     */
    @Override
    public double eval(VarTable vars) {
        return value;
    }

    /**
     * No operations are required to evaluate a Constant's value.
     */
    @Override
    public int opCount() {
        return 0;
    }

    /**
     * Return the decimal representation of this node's value (with sufficient precision to
     * reproduce its binary value).
     */
    @Override
    public String infixString() {
        return String.valueOf(value);
    }

    /**
     * Return the decimal representation of this node's value (with sufficient precision to
     * reproduce its binary value).
     */
    @Override
    public String postfixString() {
        return String.valueOf(value);
    }

    /**
     * Return whether `other` is a Constant of the same class with the same value.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        Constant c = (Constant) other;
        return c.value == value;
    }

    /**
     * A Constant has no dependencies.
     */
    @Override
    public Set<String> dependencies() {
        // Return the empty set
        return Set.of();
        // Alternative: return new HashSet<>();
    }

    /**
     * Return self (a Constant is already fully optimized).
     */
    @Override
    public Expression optimize(VarTable vars) {
        return this;
    }

}
