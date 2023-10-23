package cs2110;

/**
 * Represents a binary arithmetic operator on real numbers.  Interface also defines singleton
 * operators for common operations.
 */
public interface Operator {

    /**
     * Return the result of evaluating the operation on left operand `operand1` and right operand
     * `operand2`.
     */
    double operate(double operand1, double operand2);

    /**
     * Return the symbol used to represent this operator in expression strings.  For example, the
     * "plus" operator would have symbol "+".
     */
    String symbol();

    /**
     * Return a known operator given its symbol, `op`.  Guaranteed to recognize "+", "-", "*", "/",
     * "^".
     */
    static Operator fromString(String op) {
        return switch (op) {
            case ADD_SYMBOL -> ADD;
            case SUBTRACT_SYMBOL -> SUBTRACT;
            case MULTIPLY_SYMBOL -> MULTIPLY;
            case DIVIDE_SYMBOL -> DIVIDE;
            case POW_SYMBOL -> POW;
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };
    }

    /**
     * Return whether `symbol` is a recognized operator symbol (i.e., one that could be passed to
     * `fromString()`). Guaranteed to recognize "+", "-", "*", "/", "^".
     */
    static boolean isOperator(String symbol) {
        return symbol.equals(ADD_SYMBOL) ||
                symbol.equals(SUBTRACT_SYMBOL) ||
                symbol.equals(MULTIPLY_SYMBOL) ||
                symbol.equals(DIVIDE_SYMBOL) ||
                symbol.equals(POW_SYMBOL);
    }

    /* Recognized operator symbols. */
    /* Note: All variables declared in an interface are static and final. */
    String ADD_SYMBOL = "+";
    String SUBTRACT_SYMBOL = "-";
    String MULTIPLY_SYMBOL = "*";
    String DIVIDE_SYMBOL = "/";
    String POW_SYMBOL = "^";

    /**
     * Operator for addition.
     */
    Operator ADD = new Operator() {
        public double operate(double operand1, double operand2) {
            return operand1 + operand2;
        }

        public String symbol() {
            return ADD_SYMBOL;
        }
    };

    /**
     * Operator for subtraction.
     */
    Operator SUBTRACT = new Operator() {
        public double operate(double operand1, double operand2) {
            return operand1 - operand2;
        }

        public String symbol() {
            return SUBTRACT_SYMBOL;
        }
    };

    /**
     * Operator for multiplication.
     */
    Operator MULTIPLY = new Operator() {
        public double operate(double operand1, double operand2) {
            return operand1 * operand2;
        }

        public String symbol() {
            return MULTIPLY_SYMBOL;
        }
    };

    /**
     * Operator for division.
     */
    Operator DIVIDE = new Operator() {
        public double operate(double operand1, double operand2) {
            return operand1 / operand2;
        }

        public String symbol() {
            return DIVIDE_SYMBOL;
        }
    };

    /**
     * Operator for exponentiation.
     */
    Operator POW = new Operator() {
        public double operate(double operand1, double operand2) {
            return Math.pow(operand1, operand2);
        }

        public String symbol() {
            return POW_SYMBOL;
        }
    };
}
