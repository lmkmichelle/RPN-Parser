package cs2110;

/**
 * Indicates that a function name was encountered during parsing that had no known definition.
 */
public class UndefinedFunctionException extends Exception {

    /**
     * The name of the undefined function that was encountered (no "()" suffix).
     */
    private final String name;

    /**
     * Create a new UndefinedFunctionExpression indicating that the function `name` was encountered
     * but had no definition.
     */
    public UndefinedFunctionException(String name) {
        super("Function '" + name + "' has not been defined.");
        this.name = name;
    }

    /**
     * Return the name of the undefined function that lead to this exception.
     */
    public String name() {
        return name;
    }
}
