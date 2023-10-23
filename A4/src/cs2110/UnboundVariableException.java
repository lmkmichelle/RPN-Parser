package cs2110;

/**
 * Indicates that a variable needed to be evaluated, but no value had been assigned to it.
 */
public class UnboundVariableException extends Exception {

    /**
     * The name of the variable that had no assigned value.
     */
    private final String name;

    /**
     * Create a new UnboundVariableException indicating that the variable `name` was evaluated but
     * had no assigned value.
     */
    public UnboundVariableException(String name) {
        super("Variable '" + name + "' has not been assigned a value.");
        this.name = name;
    }

    /**
     * Return the name of the unbound variable that lead to this exception.
     */
    public String name() {
        return name;
    }
}
