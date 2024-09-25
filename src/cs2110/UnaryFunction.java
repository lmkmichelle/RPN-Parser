package cs2110;

import java.util.Map;
import java.util.function.DoubleUnaryOperator;

/**
 * Represents a named function from real numbers to real numbers.
 */
public class UnaryFunction {

    /**
     * The name of this function; e.g., "sqrt".
     */
    private final String name;

    /**
     * The operation performed by this function.
     */
    private final DoubleUnaryOperator f;

    public UnaryFunction(String name, DoubleUnaryOperator f) {
        this.name = name;
        this.f = f;
    }

    /**
     * Evaluate this function for the argument value `x`.
     */
    public double apply(double x) {
        return f.applyAsDouble(x);
    }

    /**
     * Return this function's name.  Does not include the "()" suffix that would be appended in an
     * expression context.
     */
    public String name() {
        return name;
    }

    /**
     * Create a UnaryFunction with name `name` that, when applied, will evaluate `expr`, with its
     * argument value bound to the variable `param`.  Requires that expr contain at most one
     * variable name (which must be `param`).
     */
    public static UnaryFunction fromExpression(String name, Expression expr, String param) {
        Expression opt = expr.optimize(MapVarTable.empty());
        return new UnaryFunction(name, x -> {
            try {
                return opt.eval(MapVarTable.of(param, x));
            } catch (UnboundVariableException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /* Define some common math functions for convenience */
    public static final UnaryFunction ABS = new UnaryFunction("abs", Math::abs);
    public static final UnaryFunction SQRT = new UnaryFunction("sqrt", Math::sqrt);
    public static final UnaryFunction EXP = new UnaryFunction("exp", Math::exp);
    public static final UnaryFunction LOG = new UnaryFunction("log", Math::log);
    public static final UnaryFunction SIN = new UnaryFunction("sin", Math::sin);
    public static final UnaryFunction COS = new UnaryFunction("cos", Math::cos);
    public static final UnaryFunction TAN = new UnaryFunction("tan", Math::tan);

    /**
     * Return a collection of core mathematical functions, each mapped to their name in
     * `java.lang.Math`.  Guaranteed to include at least abs, sqrt, exp, log, sin, cos, tan.
     */
    public static Map<String, UnaryFunction> mathDefs() {
        return Map.of(ABS.name(), ABS,
                SQRT.name(), SQRT,
                EXP.name(), EXP,
                LOG.name(), LOG,
                SIN.name(), SIN,
                COS.name(), COS,
                TAN.name(), TAN);
    }
}
