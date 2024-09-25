package cs2110;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A VarTable implemented using a Map from the Java Collections Framework.
 */
public class MapVarTable implements VarTable {

    Map<String, Double> map;

    /**
     * Create an empty MapVarTable.
     */
    public MapVarTable() {
        map = new HashMap<>();
    }

    /**
     * Create an empty MapVarTable.
     */
    public static MapVarTable empty() {
        return new MapVarTable();
    }

    /**
     * Create a MapVarTable associating `value1` with variable `name1`.
     */
    public static MapVarTable of(String name1, double value1) {
        MapVarTable ans = new MapVarTable();
        ans.set(name1, value1);
        return ans;
    }

    /**
     * Create a MapVarTable associating `value1` with variable `name1` and `value2` with `name2`.
     */
    public static MapVarTable of(String name1, double value1, String name2, double value2) {
        MapVarTable ans = new MapVarTable();
        ans.set(name1, value1);
        ans.set(name2, value2);
        return ans;
    }

    @Override
    public double get(String name) throws UnboundVariableException {
        Double value = map.get(name);
        if (value == null) {
            throw new UnboundVariableException(name);
        }
        return value;
    }

    @Override
    public void set(String name, double value) {
        map.put(name, value);
    }

    @Override
    public void unset(String name) {
        map.remove(name);
    }

    @Override
    public boolean contains(String name) {
        return map.containsKey(name);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Set<String> names() {
        return map.keySet();
    }
}
