package cs2110;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * An interactive calculator for evaluating expressions represented in Reverse Polish Notation.
 */
public class RpnCalc {

    /**
     * Variables and their assigned values that have been set by the user.
     */
    private VarTable vars;

    /**
     * Function names that are allowed in expressions, paired with their implementations.
     */
    private final Map<String, UnaryFunction> defs;

    /**
     * The user's current expression to be used in commands.
     */
    private Expression expr;

    /**
     * Whether the user has entered an "exit" command.
     */
    private boolean exitRequested = false;

    /**
     * Create a new calculator object with no variables set and an initial expression of "0" that
     * understands the core math functions defined in `UnaryFunction`.
     */
    public RpnCalc() {
        // Initially no variables are set.
        vars = new MapVarTable();

        // Initially only core math functions are registered.
        defs = new HashMap<>();
        defs.putAll(UnaryFunction.mathDefs());

        // Initially set the user's expression to "0" (so it's never null).
        expr = new Constant(0);
    }

    /**
     * Register the function `f` as being allowed in future expressions entered into this
     * calculator.
     */
    private void registerDef(UnaryFunction f) {
        // Register the function in `defs` under its own name.
        defs.put(f.name(), f);
    }

    /**
     * Parse all remaining tokens in `scanner` as an RPN expression and save the result as the
     * user's current expression.  If there are no tokens remaining in the scanner, the user's
     * expression is unchanged.  Throws if the tokens in the scanner do not represent a valid RPN
     * expression or invoke an unknown function.
     * <p>
     * This behavior makes it easy for commands to reuse the previous expression by making their
     * expression an optional argument.
     */
    private void updateExpr(Scanner scanner)
            throws IncompleteRpnException, UndefinedFunctionException {
        if (scanner.hasNext()) {
            expr = RpnParser.parse(scanner.nextLine(), defs);
        }
        // If there are no tokens, leave `expr` unchanged.
    }

    private void printUndefinedFunctionError(String name) {
        System.err.println("Cannot parse expression that invokes undefined function " + name);
        if (defs.isEmpty()) {
            System.err.println("No functions are currently defined");
        } else {
            System.err.println("Currently defined functions:");
            for (String defName : defs.keySet()) {
                System.err.println("* " + defName + "()");
            }
        }
    }

    private void printUnboundVariableError(String name) {
        System.err.println("Cannot evaluate expression without first setting " + name);
        if (vars.size() == 0) {
            System.err.println("No variables are currently assigned");
        } else {
            System.err.println("Currently assigned variables:");
            for (String varName : vars.names()) {
                try {
                    System.out.println("* " + varName + " = " + vars.get(varName));
                } catch (UnboundVariableException rethrown) {
                    // Since we are only querying variables known to be in this table, this should
                    // not happen.
                    throw new RuntimeException(rethrown);
                }
            }
        }
    }

    /**
     * Consume the first token in `scanner`, interpret it as a command name, and execute the
     * appropriate command handler.  Do nothing if `scanner` has no tokens.  If the command name is
     * not recognized, print an error message.
     */
    public void dispatchCommand(Scanner scanner) {
        if (!scanner.hasNext()) {
            return;
        }
        String command = scanner.next();
        switch (command) {
            case "set" -> doSet(scanner);
            case "unset" -> doUnset(scanner);
            case "clear" -> doClear(scanner);
            case "vars" -> doVars(scanner);
            case "defs" -> doDefs(scanner);
            case "eval" -> doEval(scanner);
            case "infix" -> doInfix(scanner);
            case "postfix" -> doPostfix(scanner);
            case "deps" -> doDeps(scanner);
            case "opcount" -> doOpcount(scanner);
            case "optimize" -> doOptimize(scanner);
            case "tabulate" -> doTabulate(scanner);
            case "def" -> doDef(scanner);
            case "help" -> doHelp(scanner);
            case "exit" -> exitRequested = true;
            default -> System.err.println("Unknown command: " + command);
        }
    }

    /**
     * Return whether the user has issued an 'exit' command.
     */
    public boolean exitRequested() {
        return exitRequested;
    }

    /**
     * Print the commands understood by this calculator, their arguments (in angle brackets, with
     * additional square brackets around optional arguments), and a brief description of the
     * commands' effects.  Arguments passed in `scanner` are currently ignored.
     */
    public void doHelp(Scanner scanner) {
        System.out.println("""
                set <var> <value>\t\t\tAssign <value> to the variable <var>
                unset <var>\t\t\t\tRemove any value assigned to <var>
                clear\t\t\t\t\tClear all values assigned to variables
                vars\t\t\t\t\tPrint all assigned variables and their values
                defs\t\t\t\t\tPrint all defined functions
                eval [<expr>]\\t\\t\\t\\tEvaluate <expr> (or the last expression)
                infix [<expr>]\t\t\t\tPrint <expr> (or the last expression) in infix notation
                postfix [<expr>]\t\t\tPrint <expr> (or the last expression) in postfix notation
                deps [<expr>]\t\t\t\tPrint the variables that <expr> (or the last expression) depends on
                opcount [<expr>]\t\t\tCount the number of operations needed to evaluate <expr> (or the last expression)
                optimize [<expr>]\t\t\tReplace the user's expression with the optimized form of <expr> (or the last expression)
                tabulate <var> <lo> <hi> <n> [<expr>]\tEvaluate <expr> (or the last expression) at <n> values of <var> between <lo> and <hi>
                def <name> <var> [<expr>]\t\tDefine a new function named <name> that evaluates <expr> (or the last expression) with <var> set to its argument
                exit\t\t\t\t\tExit the program""");
    }

    /* Commands related to variables */

    /**
     * Parse remaining arguments in `scanner` and perform the "set" command to assign a value to a
     * variable in this calculator instance.  Prints usage to `System.err` and returns if improper
     * arguments are passed.
     */
    public void doSet(Scanner scanner) {
        try {
            // Parse arguments (may throw)
            String name = scanner.next();
            double value = scanner.nextDouble();

            // Check for extra arguments.
            // If any, print usage and do not perform the command's action.
            if (scanner.hasNext()) {
                System.err.println("Expected: <name> <value>");
                return;
            }

            vars.set(name, value);
        } catch (InputMismatchException e) {
            System.err.println(e.getMessage());
        } catch (NoSuchElementException e) {
            System.err.println("Expected: <name> <value>");
        }
    }

    /**
     * Parse remaining arguments in `scanner` and perform the "unset" command to unassign a value
     * from a variable in this calculator instance.  Prints usage to `System.err` and returns if
     * improper arguments are passed.
     */
    public void doUnset(Scanner scanner) {
        try {
            // Parse arguments (may throw)
            String name = scanner.next();

            // Check for extra arguments.
            // If any, print usage and do not perform the command's action.
            if (scanner.hasNext()) {
                System.err.println("Expected: <name>");
                return;
            }

            vars.unset(name);
        } catch (NoSuchElementException e) {
            System.err.println("Expected: <name>");
        }
    }

    /**
     * Parse remaining arguments in `scanner` and perform the "clear" command to clear all variable
     * assignments in this calculator instance.  Prints usage to `System.err` and returns if
     * improper arguments are passed.
     */
    public void doClear(Scanner scanner) {
        // Check for extra arguments.
        // If any, print usage and do not perform the command's action.
        if (scanner.hasNext()) {
            System.err.println("Expected no arguments");
            return;
        }

        // Replace the variable table with an empty one.
        vars = new MapVarTable();
    }

    /**
     * Parse remaining arguments in `scanner` and perform the "vars" command to print all variable
     * assignments in this calculator instance.  Prints usage to `System.err` and returns if
     * improper arguments are passed.
     */
    public void doVars(Scanner scanner) {
        // Check for extra arguments.
        // If any, print usage and do not perform the command's action.
        if (scanner.hasNext()) {
            System.err.println("Expected no arguments");
            return;
        }

        for (String varName : vars.names()) {
            try {
                System.out.println("* " + varName + " = " + vars.get(varName));
            } catch (UnboundVariableException rethrown) {
                // Since we are only querying variables known to be in this table, this should not
                // happen.
                throw new RuntimeException(rethrown);
            }
        }
    }


    /**
     * Parse remaining arguments in `scanner` and perform the "defs" command to print all function
     * definitions in this calculator instance.  Prints usage to `System.err` and returns if
     * improper arguments are passed.
     */
    public void doDefs(Scanner scanner) {
        // Check for extra arguments.
        // If any, print usage and do not perform the command's action.
        if (scanner.hasNext()) {
            System.err.println("Expected no arguments");
            return;
        }

        for (String defName : defs.keySet()) {
            System.out.println("* " + defName + "()");
        }
    }

    /* Commands related to expressions */

    /**
     * Perform the "eval" command to evaluate the current expression and print the result. If
     * arguments are provided in `scanner`, parse them as an RPN expression and update the current
     * expression.  Prints to `System.err` and returns if expression cannot be parsed or depends on
     * variables that have not been assigned a value.
     */
    public void doEval(Scanner scanner) {
        try {
            updateExpr(scanner);
            System.out.println(expr.eval(vars));
        } catch (IncompleteRpnException e) {
            System.err.println(e.getMessage());
        } catch (UndefinedFunctionException e) {
            printUndefinedFunctionError(e.name());
        } catch (UnboundVariableException e) {
            printUnboundVariableError(e.name());
        }
    }

    /**
     * Perform the "infix" command to print the current expression in infix notation. If arguments
     * are provided in `scanner`, parse them as an RPN expression and update the current expression.
     * Prints to `System.err` and returns if expression cannot be parsed.
     */
    public void doInfix(Scanner scanner) {
        try {
            updateExpr(scanner);
            System.out.println(expr.infixString());
        } catch (UndefinedFunctionException e) {
            printUndefinedFunctionError(e.name());
        } catch (IncompleteRpnException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Perform the "postifx" command to print the current expression in postfix (RPN) notation. If
     * arguments are provided in `scanner`, parse them as an RPN expression and update the current
     * expression.  Prints to `System.err` and returns if expression cannot be parsed.
     */
    public void doPostfix(Scanner scanner) {
        try {
            updateExpr(scanner);
            System.out.println(expr.postfixString());
        } catch (IncompleteRpnException e) {
            System.err.println(e.getMessage());
        } catch (UndefinedFunctionException e) {
            printUndefinedFunctionError(e.name());
        }
    }

    /**
     * Perform the "deps" command to print the names of variables that the current expression
     * depends on. If arguments are provided in `scanner`, parse them as an RPN expression and
     * update the current expression.  Prints to `System.err` and returns if expression cannot be
     * parsed.
     */
    public void doDeps(Scanner scanner) {
        try {
            updateExpr(scanner);
            System.out.println(expr.dependencies());
        } catch (IncompleteRpnException e) {
            System.err.println(e.getMessage());
        } catch (UndefinedFunctionException e) {
            printUndefinedFunctionError(e.name());
        }
    }

    /**
     * Perform the "opcount" command to print the number of operations required to evaluate the
     * current expression. If arguments are provided in `scanner`, parse them as an RPN expression
     * and update the current expression.  Prints to `System.err` and returns if expression cannot
     * be parsed.
     */
    public void doOpcount(Scanner scanner) {
        try {
            updateExpr(scanner);
            System.out.println(expr.opCount());
        } catch (IncompleteRpnException e) {
            System.err.println(e.getMessage());
        } catch (UndefinedFunctionException e) {
            printUndefinedFunctionError(e.name());
        }
    }

    /**
     * Perform the "optimize" command to replace the current expression with its optimized form,
     * propagating constant subexpressions based on the current variable assignments. If arguments
     * are provided in `scanner`, parse them as an RPN expression and update the current expression.
     * Prints to `System.err` and returns if expression cannot be parsed.
     */
    public void doOptimize(Scanner scanner) {
        try {
            updateExpr(scanner);
            expr = expr.optimize(vars);
        } catch (IncompleteRpnException e) {
            System.err.println(e.getMessage());
        } catch (UndefinedFunctionException e) {
            printUndefinedFunctionError(e.name());
        }
    }

    /* Challenge extensions */

    /**
     * Parse remaining arguments in `scanner` and perform the "tabulate" command to evaluate the
     * current expression at a range of abscissa and print each abscissa and ordinate value. The
     * required arguments are:
     * <ol>
     *     <li>var: The name of the abscissa variable</li>
     *     <li>lo: The minimum value of the abscissa (floating-point number)</li>
     *     <li>hi: The maximum value of the abscissa (floating-point number)</li>
     *     <li>n: The number of abscissa to sample at (integer)</li>
     * </ol>
     * If additional arguments are provided in `scanner`, parse them as an RPN expression and update
     * the current expression.
     * <p>
     * The `n` sampled abscissa are evenly spaced between `lo` and `hi`.  After executing this
     * command,`var` should be assigned the value `hi`.  Prints  to `System.err` and returns if
     * improper arguments are passed, if expression cannot be parsed, or if expression depends on
     * variables that have not been assigned a value.
     */
    public void doTabulate(Scanner scanner) {
        // TODO (challenge extension): Implement this method according to its specification.
        // Use the helper function `updateExpr()` when appropriate.
        System.err.println("Sorry, this command is not yet supported.");
    }

    /**
     * Parse remaining arguments in `scanner` and perform the "def" command to define a new function
     * equivalent to the current expression. The required arguments are:
     * <ol>
     *     <li>name: The name of the function to define</li>
     *     <li>var: The name of the variable to serve as the function's argument</li>
     * </ol>
     * If additional arguments are provided in `scanner`, parse them as an RPN expression and update
     * the current expression.
     * <p>
     * Prints  to `System.err` and returns if improper arguments are passed, if expression cannot be
     * parsed, if expression depends on variables other than var, or if a function named `name` has
     * already been defined (in the latter two cases, the current expression is still updated).
     */
    public void doDef(Scanner scanner) {
        // TODO (challenge extension): Implement this method according to its specification.
        // Use the helper functions `updateExpr()` and `registerDef()` when appropriate.
        System.err.println("Sorry, this command is not yet supported.");
    }


    /**
     * Run an interactive calculator application.  If a program argument is provided, commands are
     * read from a file rather than `System.in`.
     */
    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Usage: java RpnCalc [<input_file>]");
            System.exit(1);
        }

        RpnCalc calc = new RpnCalc();

        // Whether to print prompts to standard output
        boolean interactive = true;
        // Create Reader to either read from standard input or read from a file.
        Reader reader = new InputStreamReader(System.in);
        if (args.length > 0) {
            interactive = false;
            String filename = args[0];
            try {
                reader = new FileReader(filename);
            } catch (FileNotFoundException e) {
                System.err.println("Could not read input from file '" + filename + "': " +
                        e.getMessage());
                System.exit(1);
            }
        }

        try (BufferedReader in = new BufferedReader(reader)) {
            // Print user prompt
            if (interactive) {
                System.out.print("> ");
                System.out.flush();
            }

            // Read and process user input one line at a time
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                // If reading commands from a file, echo the command being executed
                if (!interactive) {
                    System.out.println("> " + line);
                }

                Scanner scanner = new Scanner(line);
                calc.dispatchCommand(scanner);

                if (calc.exitRequested()) {
                    break;
                }

                // Print user prompt
                if (interactive) {
                    System.out.print("> ");
                    System.out.flush();
                }
            }
            System.out.println("Bye!");
        } catch (IOException e) {
            System.err.println("Unrecoverable error reading user input: " + e.getMessage());
            System.err.println("Exiting application");
            System.exit(1);
        }
    }
}
