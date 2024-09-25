package cs2110;

import java.util.Iterator;
import java.util.Scanner;

/**
 * Represents a single token (e.g., a number, variable name, operator symbol, or function name)
 * within an expression string.
 */
public abstract class Token {

    /**
     * The substring corresponding to the token.
     */
    protected final String value;

    /**
     * Initialize inherited value field to `value`.
     */
    protected Token(String value) {
        this.value = value;
    }

    /**
     * Return the substring that this token corresponds to.
     */
    public String value() {
        return value;
    }

    /**
     * Create a new Token of the appropriate class corresponding to the substring `value`. Valid
     * decimal numbers will yield a Number token; recognized operator symbols will yield an Operator
     * token; values ending in "()" will yield a Function token, and all other values will yield a
     * Variable token.
     */
    public static Token parse(String value) {
        if (Operator.validOperator(value)) {
            return new Operator(value);
        } else if (Number.validNumber(value)) {
            return new Number(value);
        } else if (value.endsWith("()")) {
            return new Function(value);
        } else {
            return new Variable(value);
        }
    }

    /**
     * Return the sequence of whitespace-separated tokens contained in `str`.
     */
    public static Iterable<Token> tokenizer(String str) {
        return new Iterable<>() {
            public Iterator<Token> iterator() {
                Scanner scanner = new Scanner(str);
                return new Iterator<>() {
                    public boolean hasNext() {
                        return scanner.hasNext();
                    }

                    public Token next() {
                        return Token.parse(scanner.next());
                    }
                };
            }
        };
    }

    /**
     * A token representing the name of a variable.
     */
    public static class Variable extends Token {

        /**
         * Construct a Variable token whose name is `value`.
         */
        private Variable(String value) {
            super(value);
        }
    }

    /**
     * A token representing a function call.
     */
    public static class Function extends Token {

        /**
         * Construct a Function token whose name, followed by the suffix "()", is `value`.
         */
        private Function(String value) {
            super(value);
            assert value.endsWith("()");
        }

        /**
         * Return the name of the function represented by this token.  The name will not include the
         * "()" suffix.
         */
        public String name() {
            return value.substring(0, value.length() - 2);
        }
    }

    /**
     * A token representing a floating-point number.
     */
    public static class Number extends Token {

        /**
         * Construct a new Number token whose value is represented by `value`.  Requires `value` is
         * a valid representation of a floating-point number (as determined by `validNumber()`).
         */
        private Number(String value) {
            super(value);
            assert validNumber(value);
        }

        /**
         * Return the numeric value represented by this token, in double precision.
         */
        public double doubleValue() {
            return Double.parseDouble(value);
        }

        /**
         * Return whether `value` represents  a valid floating-point number (as determined by Java's
         * `Double.valueOf()`).
         */
        public static boolean validNumber(String value) {
            try {
                Double.parseDouble(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    /**
     * A token representing a binary arithmetic operator.
     */
    public static class Operator extends Token {

        /**
         * Construct an Operator token whose symbol is `value`.  Requires `value` is a valid
         * operator symbol (as determined by `validOperator()`).
         */
        private Operator(String value) {
            super(value);
            assert validOperator(value);
        }

        /**
         * Return the operator represented by this token.
         */
        public cs2110.Operator opValue() {
            return cs2110.Operator.fromString(value);
        }

        /**
         * Return whether `value` represents a binary arithmetic operator recognized by the
         * `Operator` class.
         */
        public static boolean validOperator(String value) {
            return cs2110.Operator.isOperator(value);
        }
    }
}
