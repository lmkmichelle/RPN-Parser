package cs2110;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

public class CsvEvaluator {

    /**
     * Copy the spreadsheet data (represented as CSV records) from `parser` to `printer`, replacing
     * any formula cells with their evaluated value.
     * <p>
     * A formula cell is any cell whose first character is '='.  The remainder of the cell is
     * interpreted as a formula in Reverse Polish Notation with each token separated by whitespace.
     * The formula may refer to values of other cells by the cell's coordinates (column letter + row
     * number; e.g., "B4").  Only cells above or to the left (on the same line) of the formula cell
     * can be evaluated.  If the formula cannot be evaluated for any reason, its contents are
     * replaced by "#N/A" in the output.  Otherwise, its contents are replaced by Java's String
     * representation of the formula's value.
     * <p>
     * For a cell to be used in a later formula, its contents must represent a floating-point number
     * (as understood by Java's `Double.parseDouble()`), or else it must be a formula itself.
     */
    public static void evaluateCsv(CSVParser parser, CSVPrinter printer) throws IOException {
        // Support the most common math functions when parsing expressions.
        Map<String, UnaryFunction> defs = UnaryFunction.mathDefs();

        // A mapping of the coordinates of cells we have seen so far to their numerical values (if
        // they are a number or a successfully evaluated formula).
        VarTable vars = new MapVarTable();

        // TODO: Implement this method according to its specification.
        throw new UnsupportedOperationException();

        // Note that `CSVParser` implements `Iterable<CSVRecord>` and that `CSVRecord` implements
        // `Iterable<String>`.  This may suggest a solution using "enhanced for-loops" (though this
        // is not strictly required).
    }

    /**
     * Return the base-26 bijective numeration of `n` using the digits 'A'-'Z'.  Requires `n` is
     * non-negative.  0 is represented as the empty string.
     */
    public static String colToLetters(int n) {
        // Implementation constraint: this method must be implemented recursively.

        // Bijective hexavigesimal (base-26) numeration using the digits 'A'-'Z'.
        // Let x be the largest multiple of the base strictly less than n.
        // The rightmost digit of the representation is the difference between x and n (the
        // "remainder" - may be equal to the base).
        // The left digits are the representation of x divided by the base (the "quotient").

        // TODO: Implement this method according to its specification.
        throw new UnsupportedOperationException();
    }

    /**
     * A custom CSV format that resembles our "Simplified CSV" format from A3.  Values are never
     * quoted (because Commons CSV quotes too many things, like empty strings and '#' characters,
     * even in MINIMAL mode, which could confuse testers); instead, delimiters and record separators
     * are escaped with a backslash if necessary.
     */
    static final CSVFormat SIMPLIFIED_CSV = CSVFormat.RFC4180.builder()
            .setEscape('\\')
            .setQuoteMode(QuoteMode.NONE)
            .setRecordSeparator('\n')
            .build();

    /**
     * Parse a CSV file whose name is provided as the sole program argument, then print its
     * contents, evaluating any cells containing formulas, to the standard output stream (also in
     * CSV format).
     */
    public static void main(String[] args) throws IOException {
        // Ensure that the user provided the expected number of program arguments, then extract
        // those arguments.
        if (args.length != 1) {
            System.err.println("Usage: java CsvEvaluator <infile>");
            System.exit(1);
        }
        String filename = args[0];

        // Open the specified CSV file, then copy its contents, with formulas evaluated, to
        // `System.out`.
        try (FileReader reader = new FileReader(filename);
                CSVParser parser = SIMPLIFIED_CSV.parse(reader)) {
            // We don't open the Printer as a "resource" because we don't want to automatically
            // close `System.out`.  Instead, we flush it manually when we are done writing.
            CSVPrinter printer = SIMPLIFIED_CSV.printer();
            evaluateCsv(parser, printer);
            printer.flush();
        }
    }
}
