package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RpnParserTest {

    @Test
    @DisplayName("Parsing an expression consisting of a single number should yield a Constant " +
            "node with that value")
    void testParseConstant() throws IncompleteRpnException, UndefinedFunctionException {
        // Tests RpnParser when given a positive double Constant
        Expression expr = RpnParser.parse("1.5", Map.of());
        assertEquals(new Constant(1.5), expr);

        // Tests RpnParser when given a negative integer Constant
        expr = RpnParser.parse("-1", Map.of());
        assertEquals(new Constant(-1), expr);

        // Tests RpnParser when given a zero Constant
        expr = RpnParser.parse("0", Map.of());
        assertEquals(new Constant(0), expr);
    }

    @Test
    @DisplayName("Parsing an expression consisting of a single identifier should yield a " +
            "Variable node with that name")
    void testParseVariable() throws IncompleteRpnException, UndefinedFunctionException {
        Expression expr = RpnParser.parse("x", Map.of());
        assertEquals(new Variable("x"), expr);

        expr = RpnParser.parse("y", Map.of());
        assertEquals(new Variable("y"), expr);
    }

    @Test
    @DisplayName("Parsing an expression ending with an operator should yield an Operation node " +
            "evaluating to the expected value")
    void testParseOperation()
            throws UnboundVariableException, IncompleteRpnException, UndefinedFunctionException {
        Expression expr = RpnParser.parse("1 1 +", Map.of());
        assertInstanceOf(Operation.class, expr);
        assertEquals(2.0, expr.eval(MapVarTable.empty()));

        expr = RpnParser.parse("1 2 -", Map.of());
        assertInstanceOf(Operation.class, expr);
        assertEquals(-1, expr.eval(MapVarTable.empty()));

        expr = RpnParser.parse("2 1 -", Map.of());
        assertInstanceOf(Operation.class, expr);
        assertEquals(1, expr.eval(MapVarTable.empty()));

        expr = RpnParser.parse("1 2 /", Map.of());
        assertInstanceOf(Operation.class, expr);
        assertEquals(0.5, expr.eval(MapVarTable.empty()));

        expr = RpnParser.parse("2 1 /", Map.of());
        assertInstanceOf(Operation.class, expr);
        assertEquals(2, expr.eval(MapVarTable.empty()));

        expr = RpnParser.parse("2 1 2 / -", Map.of());
        assertInstanceOf(Operation.class, expr);
        assertEquals(1.5, expr.eval(MapVarTable.empty()));

        expr = RpnParser.parse("2.5 1 + 2 1.5 * /", Map.of());
        assertInstanceOf(Operation.class, expr);
        double epsilon = 0.000001d;
        assertTrue(Math.abs(expr.eval(MapVarTable.empty()) - 1.16666667) < epsilon);

        // TODO: This is not a very thorough test!  Both operands are the same, and the operator is
        // commutative.  Write additional test cases that don't have these properties.
        // You should also write a test case that requires recursive evaluation of the operands.

    }

    @Test
    @DisplayName("Parsing an expression ending with a function should yield an Application node " +
            "evaluating to the expected value")
    void testParseApplication()
            throws UnboundVariableException, IncompleteRpnException, UndefinedFunctionException {

        // sqrt(4) = 2
        Expression expr = RpnParser.parse("4 sqrt()", UnaryFunction.mathDefs());
        assertInstanceOf(Application.class, expr);
        assertEquals(2.0, expr.eval(MapVarTable.empty()));

        // sin(0) = 0
        expr = RpnParser.parse("0 sin()", UnaryFunction.mathDefs());
        assertInstanceOf(Application.class, expr);
        assertEquals(0, expr.eval(MapVarTable.empty()));

        // abs(3 - 6) = 3
        expr = RpnParser.parse("3 6 - abs()", UnaryFunction.mathDefs());
        assertInstanceOf(Application.class, expr);
        assertEquals(3, expr.eval(MapVarTable.empty()));

        // sqrt(3 + 6) = 3
        expr = RpnParser.parse("3 6 + sqrt()", UnaryFunction.mathDefs());
        assertInstanceOf(Application.class, expr);
        assertEquals(3, expr.eval(MapVarTable.empty()));

        // Postfix representation of sin(1 - (cos^2(0) + sin^2(0))) = 0
        expr = RpnParser.parse("1 0 cos() 2 ^ 0 sin() 2 ^ + - sin()", UnaryFunction.mathDefs());
        assertInstanceOf(Application.class, expr);
        assertEquals(0, expr.eval(MapVarTable.empty()));

    }

    @Test
    @DisplayName("Parsing an empty expression should throw an IncompleteRpnException")
    void testParseEmpty() {
        assertThrows(IncompleteRpnException.class, () -> RpnParser.parse("", Map.of()));
    }

    @Test
    @DisplayName("Parsing an expression that leave more than one term on the stack should throw " +
            "an IncompleteRpnException")
    void testParseIncomplete() {
        assertThrows(IncompleteRpnException.class, () -> RpnParser.parse("1 1 1 +", Map.of()));
        // Verifies that the exception is thrown when a double is used
        assertThrows(IncompleteRpnException.class, () -> RpnParser.parse("2 1 2.5 /", Map.of()));
    }

    @Test
    @DisplayName("Parsing an expression that consumes more terms than are on the stack should " +
            "throw an IncompleteRpnException")
    void testParseUnderflow() {
        assertThrows(IncompleteRpnException.class, () -> RpnParser.parse("1 1 + +", Map.of()));
        assertThrows(IncompleteRpnException.class, () -> RpnParser.parse("abs() 1 1 +", Map.of()));
    }

    @Test
    @DisplayName("Parsing an expression that applies an unknown function should throw an " +
            "UnknownFunctionException")
    void testParseUndefined() {
        assertThrows(UndefinedFunctionException.class, () -> RpnParser.parse("1 foo()", Map.of()));
        assertThrows(UndefinedFunctionException.class, () -> RpnParser.parse("3 bar()", Map.of()));
    }
}
