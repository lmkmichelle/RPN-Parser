package cs2110;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantExpressionTest {

    @Test
    @DisplayName("A Constant node should evaluate to its value (regardless of var table)")
    void testEval() throws UnboundVariableException {
        Expression expr = new Constant(1.5);
        assertEquals(1.5, expr.eval(MapVarTable.empty()));
    }


    @Test
    @DisplayName("A Constant node should report that 0 operations are required to evaluate it")
    void testOpCount() {
        Expression expr = new Constant(1.5);
        assertEquals(0, expr.opCount());
    }


    @Test
    @DisplayName("A Constant node should produce an infix representation with just its value (as " +
            "formatted by String.valueOf(double))")
    void testInfix() {
        Expression expr = new Constant(1.5);
        assertEquals("1.5", expr.infixString());

        expr = new Constant(Math.PI);
        assertEquals("3.141592653589793", expr.infixString());

        expr = new Constant(2.0);
        assertEquals("2.0", expr.infixString());
    }

    @Test
    @DisplayName("A Constant node should produce an postfix representation with just its value " +
            "(as formatted by String.valueOf(double))")
    void testPostfix() {
        Expression expr = new Constant(1.5);
        assertEquals("1.5", expr.postfixString());

        expr = new Constant(Math.PI);
        assertEquals("3.141592653589793", expr.postfixString());
    }


    @Test
    @DisplayName("A Constant node should equal itself")
    void testEqualsSelf() {
        Expression expr = new Constant(1.5);
        // Normally `assertEquals()` is preferred, but since we are specifically testing the
        // `equals()` method, we use the more awkward `assertTrue()` to make that call explicit.
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("A Constant node should equal another Constant node with the same value")
    void testEqualsTrue() {
        Expression expr1 = new Constant(1.5);
        Expression expr2 = new Constant(1.5);
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("A Constant node should not equal another Constant node with a different value")
    void testEqualsFalse() {
        Expression expr1 = new Constant(1.5);
        Expression expr2 = new Constant(2.0);
        assertFalse(expr1.equals(expr2));

        // Verifies that the equal() method returns false when tested against a null
        expr1 = null;
        assertFalse(expr2.equals(expr1));

        // Verifies that the equal() method returns false when tested against an object of a different class
        expr1 = new Variable("x");
        assertFalse(expr2.equals(expr1));
    }


    @Test
    @DisplayName("A Constant node does not depend on any variables")
    void testDependencies() {
        Expression expr = new Constant(1.5);
        Set<String> deps = expr.dependencies();
        assertTrue(deps.isEmpty());
    }


    @Test
    @DisplayName("A Constant node should optimize to itself (regardless of var table)")
    void testOptimize() {
        Expression expr = new Constant(1.5);
        Expression opt = expr.optimize(MapVarTable.empty());
        assertEquals(expr, opt);
    }
}

class VariableExpressionTest {

    @Test
    @DisplayName("A Variable node should evaluate to its variable's value when that variable is " +
            "in the var map")
    void testEvalBound() throws UnboundVariableException {
        Expression expr = new Variable("x");
        MapVarTable vars = MapVarTable.of("x", 1);
        // Verifies that eval() returns the value of Variable `x`, which is 1
        assertEquals(1, expr.eval(vars));

        // Verifies that eval() returns the value of Variable `x`, which is 1, even when there is another
        // variable in the MapVarTable
        vars.set("y", 2);
        assertEquals(1, expr.eval(vars));

        // Verifies that eval() returns the value of Variable `x`, which is 2, after it is set from 1 to 2.
        vars.set("x", 2);
        assertEquals(2, expr.eval(vars));
    }

    @Test
    @DisplayName("A Variable node should throw an UnboundVariableException when evaluated if its " +
            "variable is not in the var map")
    void testEvalUnbound() {

        // They assume that your `Variable` constructor takes its name as an argument.
        Expression expr = new Variable("x");
        assertThrows(UnboundVariableException.class, () -> expr.eval(MapVarTable.empty()));

        // Verifies that eval() throws an `UnboundVariableException` when the var map is non-empty but still
        // not containing the specified Variable
        MapVarTable vars = MapVarTable.of("y", 1);
        assertThrows(UnboundVariableException.class, () -> expr.eval(vars));
    }


    @Test
    @DisplayName("A Variable node should report that 0 operations are required to evaluate it")
    void testOpCount() {
        // Single-character variable name
        Expression expr = new Variable("x");
        assertEquals(0, expr.opCount());

        // Multi-character variable name
        expr = new Variable("hello");
        assertEquals(0, expr.opCount());
    }


    @Test
    @DisplayName("A Variable node should produce an infix representation with just its name")
    void testInfix() {
        // Single-character variable name
        Expression expr = new Variable("x");
        assertEquals("x", expr.infixString());

        // Multi-character variable name
        expr = new Variable("hello");
        assertEquals("hello", expr.infixString());
    }

    @Test
    @DisplayName("A Variable node should produce an postfix representation with just its name")
    void testPostfix() {
        // Single-character variable name
        Expression expr = new Variable("x");
        assertEquals("x", expr.postfixString());

        // Multi-character variable name
        expr = new Variable("hello");
        assertEquals("hello", expr.postfixString());
    }


    @Test
    @DisplayName("A Variable node should equal itself")
    void testEqualsSelf() {
        Expression expr = new Variable("x");
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("A Variable node should equal another Variable node with the same name")
    void testEqualsTrue() {
        Expression expr1 = new Variable(new String("x"));
        Expression expr2 = new Variable(new String("x"));
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("A Variable node should not equal another Variable node with a different name")
    void testEqualsFalse() {
        // Verifies that a Variable node should not equal another Variable node with a different name
        Expression expr1 = new Variable(new String("y"));
        Expression expr2 = new Variable(new String("x"));
        assertFalse(expr1.equals(expr2));

        // Verifies that a Variable node should not equal another non-Variable node
        expr2 = new Constant(2);
        assertFalse(expr1.equals(expr2));
    }

    @Test
    @DisplayName("A Variable node only depends on its name")
    void testDependencies() {
        Expression expr = new Variable("x");
        Set<String> deps = expr.dependencies();
        assertTrue(deps.contains("x"));
        assertEquals(1, deps.size());
    }

    @Test
    @DisplayName("A Variable node should optimize to a Constant if its variable is in the var map")
    void testOptimizeBound() {
        // Verifies that optimize() optimizes to the respective constant when the variable is in the var map
        Expression expr = new Variable("x");
        Expression opt = expr.optimize(MapVarTable.of("x", 1.5));
        assertEquals(new Constant(1.5), opt);

        // Verifies that optimize() optimizes to the respective constant when there are more than one
        // Variables in the var map.
        expr = new Variable("x");
        opt = expr.optimize(MapVarTable.of("y", 2.0, "x", 1.5));
        assertEquals(new Constant(1.5), opt);
    }

    @Test
    @DisplayName("A Variable node should optimize to itself if its variable is not in the var map")
    void testOptimizeUnbound() {
        // Tests the method using a non-empty MapVarTable while the variable is still not in the var map.
        Expression expr = new Variable("y");
        Expression opt = expr.optimize(MapVarTable.of("x", 1.5));
        assertEquals(expr, opt);

        // Tests the method using an empty MapVarTable.
        expr = new Variable("y");
        opt = expr.optimize(MapVarTable.empty());
        assertEquals(expr, opt);
    }
}

class OperationExpressionTest {

    @Test
    @DisplayName("An Operation node for ADD with two Constant operands should evaluate to their " +
            "sum")
    void testEvalAdd() throws UnboundVariableException {
        // Two positive numbers, one double one integer
        Expression expr = new Operation(Operator.ADD, new Constant(1.5), new Constant(2));
        assertEquals(3.5, expr.eval(MapVarTable.empty()));

        // One negative and one positive number
        expr = new Operation(Operator.ADD, new Constant(-1), new Constant(2));
        assertEquals(1, expr.eval(MapVarTable.empty()));

        // Two negative numbers
        expr = new Operation(Operator.ADD, new Constant(-1), new Constant(-2));
        assertEquals(-3, expr.eval(MapVarTable.empty()));

        // A zero value
        expr = new Operation(Operator.ADD, new Constant(0), new Constant(2));
        assertEquals(2, expr.eval(MapVarTable.empty()));
    }

    @Test
    @DisplayName("An Operation node for ADD with a Variable for an operand should evaluate " +
            "to its operands' sum when the variable is in the var map")
    void testEvalAddBound() throws UnboundVariableException {

        // Testing the Operation node on a Variable and a Constant
        Expression expr = new Operation(Operator.ADD, new Variable("x"), new Constant(2));
        MapVarTable vars = MapVarTable.of("x", 2);
        assertEquals(4, expr.eval(vars));

        // Testing the Operation node on two Variable objects
        expr = new Operation(Operator.ADD, new Variable("x"), new Variable("y"));
        vars.set("y", 1.5);
        assertEquals(3.5, expr.eval(vars));
    }

    @Test
    @DisplayName("An Operation node for ADD with a Variable for an operand should throw an " +
            "UnboundVariableException when evaluated if the variable is not in the var map")
    void testEvalAddUnbound() {
        // Verifies that the eval() method throws UnboundVariableException when an empty MapVarTable is passed
        Expression expr = new Operation(Operator.ADD, new Variable("x"), new Constant(2));
        assertThrows(UnboundVariableException.class, () -> expr.eval(MapVarTable.empty()));

        // Verifies that the eval() method throws UnboundVariableException when a non-empty MapVarTable is passed that
        // still does not contain the variable `x` as passed in `expr`
        MapVarTable vars = MapVarTable.of("y", 1);
        assertThrows(UnboundVariableException.class, () -> expr.eval(vars));
    }

    @Test
    @DisplayName("An Operation node with leaf operands should report that 1 operation is " +
            "required to evaluate it")
    void testOpCountLeaves() {
        // Verifies opCount() using two leaf operands, a Variable & Constant
        Expression expr = new Operation(Operator.ADD, new Variable("x"), new Constant(2));
        assertEquals(1, expr.opCount());
    }

    @Test
    @DisplayName("An Operation node with an Operation for either or both operands should report " +
            "the correct number of operations to evaluate it")
    void testOpCountRecursive() {
        Expression expr = new Operation(Operator.ADD,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")),
                new Constant(2.0));
        assertEquals(2, expr.opCount());

        expr = new Operation(Operator.SUBTRACT,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")),
                new Operation(Operator.DIVIDE, new Constant(1.5), new Variable("x")));
        assertEquals(3, expr.opCount());
    }

    @Test
    @DisplayName("An Operation node with leaf operands should produce an infix representation " +
            "consisting of its first operand, its operator symbol surrounded by spaces, and " +
            "its second operand, all enclosed in parentheses")
    void testInfixLeaves() {
        // Addition
        Expression expr = new Operation(Operator.ADD, new Variable("x"), new Constant(2));
        assertEquals("(x + 2.0)", expr.infixString());

        // Subtraction
        expr = new Operation(Operator.SUBTRACT, new Variable("x"), new Constant(2));
        assertEquals("(x - 2.0)", expr.infixString());

        // Multiplication
        expr = new Operation(Operator.MULTIPLY, new Variable("x"), new Constant(2));
        assertEquals("(x * 2.0)", expr.infixString());

        // Division
        expr = new Operation(Operator.DIVIDE, new Variable("x"), new Constant(2));
        assertEquals("(x / 2.0)", expr.infixString());

        // Power
        expr = new Operation(Operator.POW, new Variable("x"), new Constant(2));
        assertEquals("(x ^ 2.0)", expr.infixString());
    }

    @Test
    @DisplayName("An Operation node with an Operation for either operand should produce the " +
            "expected infix representation with parentheses around each operation")
    void testInfixRecursive() {

        // Commutative operations (addition & multiplication)
        Expression expr = new Operation(Operator.ADD,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")),
                new Constant(2.0));
        assertEquals("((1.5 * x) + 2.0)", expr.infixString());

        // Non-commutative operations (subtraction & division)
        expr = new Operation(Operator.SUBTRACT,
                new Constant(2.0),
                new Operation(Operator.DIVIDE, new Constant(1.5), new Variable("x")));
        assertEquals("(2.0 - (1.5 / x))", expr.infixString());
    }

    @Test
    @DisplayName("An Operation node with leaf operands should produce a postfix representation " +
            "consisting of its first operand, its second operand, and its operator symbol " +
            "separated by spaces")
    void testPostfixLeaves() {
        // Addition
        Expression expr = new Operation(Operator.ADD, new Variable("x"), new Constant(2));
        assertEquals("x 2.0 +", expr.postfixString());

        // Subtraction
        expr = new Operation(Operator.SUBTRACT, new Variable("x"), new Constant(2));
        assertEquals("x 2.0 -", expr.postfixString());

        // Multiplication
        expr = new Operation(Operator.MULTIPLY, new Variable("x"), new Constant(2));
        assertEquals("x 2.0 *", expr.postfixString());

        // Division
        expr = new Operation(Operator.DIVIDE, new Variable("x"), new Constant(2));
        assertEquals("x 2.0 /", expr.postfixString());

        // Power
        expr = new Operation(Operator.POW, new Variable("x"), new Constant(2));
        assertEquals("x 2.0 ^", expr.postfixString());
    }

    @Test
    @DisplayName("An Operation node with an Operation for either operand should produce the " +
            "expected postfix representation")
    void testPostfixRecursive() {
        // Commutative operations (addition & multiplication)
        Expression expr = new Operation(Operator.ADD,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")),
                new Constant(2.0));
        assertEquals("1.5 x * 2.0 +", expr.postfixString());

        // Non-commutative operations (subtraction & division)
        expr = new Operation(Operator.SUBTRACT,
                new Constant(2.0),
                new Operation(Operator.DIVIDE, new Constant(1.5), new Variable("x")));
        assertEquals("2.0 1.5 x / -", expr.postfixString());
    }

    @Test
    @DisplayName("An Operation node should equal itself")
    void testEqualsSelf() {
        Expression expr = new Operation(Operator.ADD, new Constant(1.5), new Variable("x"));
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("An Operation node should equal another Operation node with the same " +
            "operator and operands")
    void testEqualsTrue() {
        Expression expr1 = new Operation(Operator.ADD, new Constant(1.5), new Variable("x"));
        Expression expr2 = new Operation(Operator.ADD, new Constant(1.5), new Variable("x"));
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("An Operation node should not equal another Operation node with a different " +
            "operator")
    void testEqualsFalse() {
        // All operands are the same, only operator changed
        Expression expr1 = new Operation(Operator.SUBTRACT, new Constant(1.5), new Variable("x"));
        Expression expr2 = new Operation(Operator.ADD, new Constant(1.5), new Variable("x"));
        assertFalse(expr1.equals(expr2));
    }


    @Test
    @DisplayName("An Operation node depends on the dependencies of both of its operands")
    void testDependencies() {
        // The operands are both leaf nodes
        Expression expr = new Operation(Operator.ADD, new Variable("x"), new Variable("y"));
        Set<String> deps = expr.dependencies();
        assertTrue(deps.contains("x"));
        assertTrue(deps.contains("y"));
        assertEquals(2, deps.size());

        // The operands are both operations that each have two operand children
        expr = new Operation(Operator.ADD,
                new Operation(Operator.ADD, new Variable("x"), new Variable("w")),
                new Operation(Operator.DIVIDE, new Variable("y"), new Variable("z")));
        deps = expr.dependencies();
        assertTrue(deps.contains("x"));
        assertTrue(deps.contains("y"));
        assertTrue(deps.contains("w"));
        assertTrue(deps.contains("z"));
        assertEquals(4, deps.size());
    }


    @Test
    @DisplayName("An Operation node for ADD with two Constant operands should optimize to a " +
            "Constant containing their sum")
    void testOptimizeAdd() {
        Expression expr = new Operation(Operator.ADD, new Constant(1.5), new Constant(2.5));
        Expression opt = expr.optimize(MapVarTable.empty());
        assertEquals(new Constant(4), opt);
    }

    @Test
    @DisplayName(
            "An Operation node should optimize to a Constant when there contains Variables that are contained in "
                    + "the var map.")
    void testOptimizeBound() {
        // Addition between two bound variables
        Expression expr = new Operation(Operator.ADD, new Variable("x"), new Variable("y"));
        Expression opt = expr.optimize(MapVarTable.of("x", 1.5, "y", 2.5));
        assertEquals(new Constant(4), opt);

        // Multiplication between two bound variables
        expr = new Operation(Operator.MULTIPLY, new Variable("x"), new Variable("y"));
        opt = expr.optimize(MapVarTable.of("x", 4, "y", 2));
        assertEquals(new Constant(8), opt);

        // Division between two bound variables
        expr = new Operation(Operator.DIVIDE, new Variable("x"), new Variable("y"));
        opt = expr.optimize(MapVarTable.of("x", 4, "y", 2));
        assertEquals(new Constant(2), opt);

        // The first variable raised to the power of the second variable
        expr = new Operation(Operator.POW, new Variable("x"), new Variable("y"));
        opt = expr.optimize(MapVarTable.of("x", 4, "y", 2));
        assertEquals(new Constant(16), opt);

        // The first variable minus the second variable
        expr = new Operation(Operator.SUBTRACT, new Variable("x"), new Variable("y"));
        opt = expr.optimize(MapVarTable.of("x", 4, "y", 2));
        assertEquals(new Constant(2), opt);

        // Performs addition on the same variable onto itself
        expr = new Operation(Operator.ADD, new Variable("x"), new Variable("x"));
        opt = expr.optimize(MapVarTable.of("x", 1.5));
        assertEquals(new Constant(3), opt);

        // Multiplies the same variable by itself
        expr = new Operation(Operator.MULTIPLY, new Variable("x"), new Variable("x"));
        opt = expr.optimize(MapVarTable.of("x", 2));
        assertEquals(new Constant(4), opt);
    }

    @Test
    @DisplayName(
            "An Operation node should optimize to an Operation node when there contains Variables that are not "
                    + "contained in the var map.")
    void testOptimizeUnbound() {
        // Optimizes the equation (x + 2 * 3) / (y - 1) into (x + 6) / 1
        Expression left = new Operation(Operator.ADD, new Variable("x"),
                new Operation(Operator.MULTIPLY, new Constant(2), new Constant(3)));
        Expression right = new Operation(Operator.SUBTRACT, new Variable("y"), new Constant(1));
        Expression expr = new Operation(Operator.DIVIDE, left, right);
        Expression opt = expr.optimize(MapVarTable.of("y", 2));
        assertEquals(new Operation(Operator.DIVIDE,
                new Operation(Operator.ADD, new Variable("x"), new Constant(6)),
                new Constant(1)), opt);
    }
}

class ApplicationExpressionTest {

    @Test
    @DisplayName("An Application node for SQRT with a Constant argument should evaluate to its " +
            "square root")
    void testEvalSqrt() throws UnboundVariableException {
        // Verifies that an Application node with a SQRT & Constant argument evaluates to its square root
        Expression expr = new Application(UnaryFunction.SQRT, new Constant(4));
        assertEquals(2.0, expr.eval(MapVarTable.empty()));

        // Verifies that a square root of a negative number returns `NaN`.
        expr = new Application(UnaryFunction.SQRT, new Constant(-4));
        assertTrue(((Double) expr.eval(MapVarTable.empty())).isNaN());
    }

    @Test
    @DisplayName("An Application node with a Variable for its argument should throw an " +
            "UnboundVariableException when evaluated if the variable is not in the var map")
    void testEvalAbsUnbound() {
        // Verifies that `UnboundVariableException` is thrown when eval() is passed with an empty var map
        Expression expr = new Application(UnaryFunction.ABS, new Variable("x"));
        assertThrows(UnboundVariableException.class, () -> expr.eval(MapVarTable.empty()));

        // Verifies that `UnboundVariableException` is thrown when eval() is passed with a non-empty var map that
        // still does not contain the specified variable
        Expression exp = new Application(UnaryFunction.ABS, new Variable("x"));
        assertThrows(UnboundVariableException.class, () -> exp.eval(MapVarTable.of("y", 1.5)));
    }

    @Test
    @DisplayName("An Application node with a leaf argument should report that 1 operation is " +
            "required to evaluate it")
    void testOpCountLeaf() {
        // Passes a Variable node into an Application node
        Expression expr = new Application(UnaryFunction.ABS, new Variable("x"));
        assertEquals(1, expr.opCount());

        // Passes a Constant node into an Application node
        expr = new Application(UnaryFunction.ABS, new Constant(1));
        assertEquals(1, expr.opCount());
    }

    @Test
    @DisplayName("An Application node with non-leaf expressions for its argument should report " +
            "the correct number of operations to evaluate it")
    void testOpCountRecursive() {
        // Passes an Operation of multiplication between a Constant and a Variable
        Expression expr = new Application(UnaryFunction.SQRT,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")));
        assertEquals(2, expr.opCount());
    }

    @Test
    @DisplayName(
            "An Application node with a leaf argument should produce an infix representation " +
                    "consisting of its name, followed by the argument enclosed in parentheses.")
    void testInfixLeaves() {
        // Absolute value
        Expression expr = new Application(UnaryFunction.ABS, new Variable("x"));
        assertEquals("abs(x)", expr.infixString());
        // Cosine
        expr = new Application(UnaryFunction.COS, new Variable("x"));
        assertEquals("cos(x)", expr.infixString());
        // Exponential
        expr = new Application(UnaryFunction.EXP, new Variable("x"));
        assertEquals("exp(x)", expr.infixString());
        // Logarithm
        expr = new Application(UnaryFunction.LOG, new Variable("x"));
        assertEquals("log(x)", expr.infixString());
        // Square root
        expr = new Application(UnaryFunction.SQRT, new Variable("x"));
        assertEquals("sqrt(x)", expr.infixString());
        // Sine
        expr = new Application(UnaryFunction.SIN, new Variable("x"));
        assertEquals("sin(x)", expr.infixString());
        // Tangent
        expr = new Application(UnaryFunction.TAN, new Variable("x"));
        assertEquals("tan(x)", expr.infixString());

    }

    @Test
    @DisplayName("An Application node with an Operation for its argument should produce the " +
            "expected infix representation with redundant parentheses around the argument")
    void testInfixRecursive() {
        // Absolute value
        Expression expr = new Application(UnaryFunction.ABS,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")));
        assertEquals("abs((1.5 * x))", expr.infixString());
        // Cosine
        expr = new Application(UnaryFunction.COS,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")));
        assertEquals("cos((1.5 * x))", expr.infixString());
        // Exponential
        expr = new Application(UnaryFunction.EXP,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")));
        assertEquals("exp((1.5 * x))", expr.infixString());
        // Logarithm
        expr = new Application(UnaryFunction.LOG,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")));
        assertEquals("log((1.5 * x))", expr.infixString());
        // Square root
        expr = new Application(UnaryFunction.SQRT,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")));
        assertEquals("sqrt((1.5 * x))", expr.infixString());
        // Sine
        expr = new Application(UnaryFunction.SIN,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")));
        assertEquals("sin((1.5 * x))", expr.infixString());
        // Tangent
        expr = new Application(UnaryFunction.TAN,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")));
        assertEquals("tan((1.5 * x))", expr.infixString());
    }


    @Test
    @DisplayName("An Application node with a leaf argument should produce a postfix " +
            "representation consisting of its argument, followed by a space, followed by its " +
            "function's name appended with parentheses")
    void testPostfixLeaves() {
        // Absolute value
        Expression expr = new Application(UnaryFunction.ABS, new Variable("x"));
        assertEquals("x abs()", expr.postfixString());
        // Cosine
        expr = new Application(UnaryFunction.COS, new Variable("x"));
        assertEquals("x cos()", expr.postfixString());
        // Exponential
        expr = new Application(UnaryFunction.EXP, new Variable("x"));
        assertEquals("x exp()", expr.postfixString());
        // Logarithm
        expr = new Application(UnaryFunction.LOG, new Variable("x"));
        assertEquals("x log()", expr.postfixString());
        // Square root
        expr = new Application(UnaryFunction.SQRT, new Variable("x"));
        assertEquals("x sqrt()", expr.postfixString());
        // Sine
        expr = new Application(UnaryFunction.SIN, new Variable("x"));
        assertEquals("x sin()", expr.postfixString());
        // Tangent
        expr = new Application(UnaryFunction.TAN, new Variable("x"));
        assertEquals("x tan()", expr.postfixString());
    }

    @Test
    @DisplayName("An Application node with an Operation for its argument should produce the " +
            "expected postfix representation")
    void testPostfixRecursive() {
        Expression expr = new Application(UnaryFunction.ABS,
                new Operation(Operator.MULTIPLY, new Constant(1.5), new Variable("x")));
        assertEquals("1.5 x * abs()", expr.postfixString());
    }

    @Test
    @DisplayName("An Application node should equal itself")
    void testEqualsSelf() {
        Expression expr = new Application(UnaryFunction.SQRT, new Constant(4.0));
        assertTrue(expr.equals(expr));
    }

    @Test
    @DisplayName("An Application node should equal another Application node with the same " +
            "function and argument")
    void testEqualsTrue() {
        Expression expr1 = new Application(UnaryFunction.SQRT, new Constant(4.0));
        Expression expr2 = new Application(UnaryFunction.SQRT, new Constant(4.0));
        assertTrue(expr1.equals(expr2));
    }

    @Test
    @DisplayName("An Application node should not equal another Application node with a different " +
            "argument")
    void testEqualsFalseArg() {
        // Same function, different argument
        Expression expr1 = new Application(UnaryFunction.SQRT, new Constant(1.0));
        Expression expr2 = new Application(UnaryFunction.SQRT, new Constant(4.0));
        assertFalse(expr1.equals(expr2));
    }

    @Test
    @DisplayName("An Application node should not equal another Application node with a different " +
            "function")
    void testEqualsFalseFunc() {
        // Same argument, different function
        Expression expr1 = new Application(UnaryFunction.SQRT, new Constant(4.0));
        Expression expr2 = new Application(UnaryFunction.ABS, new Constant(4.0));
        assertFalse(expr1.equals(expr2));
    }

    @Test
    @DisplayName(
            "An Application node should not equal another Expression that is not an Application"
                    + "node")
    void testEqualsFalse() {
        // Compares a Constant with an Application
        Expression expr1 = new Constant(4);
        Expression expr2 = new Application(UnaryFunction.ABS, new Constant(4.0));
        assertFalse(expr2.equals(expr1));
    }

    @Test
    @DisplayName("An Application node for SQRT with a Constant argument should optimize to a " +
            "Constant containing its square root")
    void testOptimizeConstant() {
        Expression expr = new Application(UnaryFunction.SQRT, new Constant(4));
        Expression opt = expr.optimize(MapVarTable.empty());
        assertInstanceOf(Constant.class, opt);
        assertEquals(new Constant(2), opt.optimize(MapVarTable.empty()));
    }

    @Test
    @DisplayName("An Application node has the same dependencies as its argument")
    void testDependencies() {
        // Verifies using a leaf node argument
        Expression arg = new Variable("x");
        Expression expr = new Application(UnaryFunction.SQRT, arg);
        Set<String> argDeps = arg.dependencies();
        Set<String> exprDeps = expr.dependencies();
        assertEquals(argDeps, exprDeps);

        // Verifies using an Operation with two Variables as an argument
        arg = new Operation(Operator.MULTIPLY, new Variable("x"), new Variable("y"));
        expr = new Application(UnaryFunction.ABS, arg);
        argDeps = arg.dependencies();
        exprDeps = expr.dependencies();
        assertEquals(argDeps, exprDeps);
    }

    @Test
    @DisplayName("An Application node with an argument depending on a variable should optimize " +
            "to an Application node if the variable is unbound")
    void testOptimizeUnbound() {
        Expression expr = new Application(UnaryFunction.SQRT, new Variable("x"));
        Expression opt = expr.optimize(MapVarTable.empty());
        // Verifies that the optimized node is an Application node
        assertInstanceOf(Application.class, opt);
        // Verifies that the optimized node is equal to its original, since its original was already
        // fully optimized
        assertEquals(expr, opt);
        // Verifies that the resulting optimized node is optimized
        assertEquals(opt, opt.optimize(MapVarTable.empty()));
    }

    @Test
    @DisplayName("An Application node with an argument depending on a variable should optimize " +
            "to a Constant node if the variable is bound")
    void testOptimizeBound() {
        Expression expr = new Application(UnaryFunction.SQRT, new Variable("x"));
        Expression opt = expr.optimize(MapVarTable.of("x", 4));
        // Verifies that the optimized node is a Constant node
        assertInstanceOf(Constant.class, opt);
        // Verifies that the optimized node is equal to its intended output
        assertEquals(new Constant(2), opt);
        // Verifies that the resulting optimized node is optimized
        assertEquals(opt, opt.optimize(MapVarTable.empty()));
    }
}
