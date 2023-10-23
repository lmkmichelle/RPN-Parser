package cs2110;

/**
 * Indicates that an expression string could not be parsed as RPN to yield an expression.
 */
public class IncompleteRpnException extends Exception {

    /**
     * The expression string that was parsed.
     */
    private final String expression;

    /**
     * The size of the expression stack at the conclusion of parsing (presumably not 1).
     */
    private final int stackDepth;

    /**
     * Create an IncompleteRpnException indicating that `expression` could not be parsed as RPN and
     * that the expression stack had a size of `stackDepth` at the conclusion of parsing.
     */
    public IncompleteRpnException(String expression, int stackDepth) {
        super("The expression '" + expression + "' is not a complete RPN expression (" + stackDepth
                + " terms remain on stack)");
        this.expression = expression;
        this.stackDepth = stackDepth;
    }

    /**
     * The expression string that parsing was attempted on.
     */
    public String expression() {
        return expression;
    }

    /**
     * The size of the expression stack at the conclusion of parsing.
     */
    public int stackDepth() {
        return stackDepth;
    }
}
