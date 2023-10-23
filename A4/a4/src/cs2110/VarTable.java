package cs2110;

import java.util.Set;

/**
 * A collection of variable names and assigned numeric values, as could be used when evaluating
 * mathematical expressions.
 */
public interface VarTable {

    /**
     * Return the value associated with the variable `name`.  Throws UnboundVariableException if
     * `name` is not associated with a value in this table.
     */
    double get(String name) throws UnboundVariableException;

    /**
     * Associate `value` with variable `name` in this table, replacing any previously assigned
     * value.
     */
    void set(String name, double value);

    /**
     * Remove any value associated with variable `name` in this table.
     */
    void unset(String name);

    /**
     * Return whether variable `name` is currently associated with a value in this table.
     */
    boolean contains(String name);

    /**
     * Return the number of variables associated with values in this table.
     */
    int size();

    /**
     * Return the names of all variables associated with a value in this table.
     */
    Set<String> names();
}
