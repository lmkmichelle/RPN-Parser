package cs2110;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.NoSuchElementException;

public class RpnParser {

    /**
     * Parse the RPN expression in `exprString` and return the corresponding expression tree. Tokens
     * must be separated by whitespace.  Valid tokens include decimal numbers (scientific notation
     * allowed), arithmetic operators (+, -, *, /, ^), function names (with the suffix "()"), and
     * variable names (anything else).  When a function name is encountered, the corresponding
     * function will be retrieved from `funcDefs` using the name (without "()" suffix) as the key.
     *
     * @throws IncompleteRpnException     if the expression has too few or too many operands
     *                                    relative to operators and functions.
     * @throws UndefinedFunctionException if a function name applied in `exprString` is not present
     *                                    in `funcDefs`.
     */
    public static Expression parse(String exprString, Map<String, UnaryFunction> funcDefs)
            throws IncompleteRpnException, UndefinedFunctionException {
        // Each token will result in a subexpression being pushed onto this stack.  If the
        // subexpression requires arguments, they are first popped off of this stack.
        Deque<Expression> stack = new ArrayDeque<>();

        // Loop over each token in the expression string from left to right
        for (Token token : Token.tokenizer(exprString)) {
            // TODO: Based on the dynamic type of the token, create the appropriate Expression node
            // and push it onto the stack, popping arguments as needed.
            // The "number" token is done for you as an example.
            if (token instanceof Token.Number) {
                Token.Number numToken = (Token.Number) token;
                stack.push(new Constant(numToken.doubleValue()));
            }
            if (token instanceof Token.Operator) {
                // When the current token is an Operator, there must exist at least two expressions left on the stack to be
                // the two operands for the operation to continue
                if (stack.size() < 2) {
                    throw new IncompleteRpnException("There are not enough expressions left in the stack for the operand to perform"
                            + "the operation.", stack.size());
                }
                Expression right = stack.pop();
                Expression left = stack.pop();
                Token.Operator opToken = (Token.Operator) token;
                Operation op = new Operation(opToken.opValue(), left, right);
                stack.push(op);

            }
            if (token instanceof Token.Function) {

                // When the current token is an Application node, there must exist at least one expression left on the stack for
                // the argument to execute the Function
                if (stack.size() < 1) {
                    throw new IncompleteRpnException("There are not enough expressions left in the stack for the function to execute."
                            , stack.size());
                }
                Expression expr = stack.pop();
                Token.Function funcToken = (Token.Function) token;
                funcDefs = UnaryFunction.mathDefs();
                if (!funcDefs.containsKey(funcToken.name())) {
                    throw new UndefinedFunctionException("This function is not defined.");
                }
                UnaryFunction un = funcDefs.get(funcToken.name());
                Application a = new Application(un, expr);
                stack.push(a);
            }
            if (token instanceof Token.Variable) {
                Token.Variable varToken = (Token.Variable) token;
                stack.push(new Variable((token.value())));
            }
        }

        if (stack.size() != 1) {
            throw new IncompleteRpnException("The final expression does not leave exactly one expression left in the stack",
                    stack.size());
        }
        return stack.pop();
    }
}
