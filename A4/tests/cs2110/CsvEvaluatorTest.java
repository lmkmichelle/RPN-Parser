package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CsvEvaluatorTest {

    @Test
    @DisplayName("The column label for column 0 should be the empty string")
    void testColToLetters0() {
        assertEquals("", CsvEvaluator.colToLetters(0));
    }

    @Test
    @DisplayName("Column labels for columns 1-26 should consist of the correct single letter")
    void testColToLetters1() {
        assertEquals("A", CsvEvaluator.colToLetters(1));
        assertEquals("Z", CsvEvaluator.colToLetters(26));
    }

    @Test
    @DisplayName("Column labels for columns 27-704 should consist of the correct two letters")
    void testColToLetters2() {
        assertEquals("AA", CsvEvaluator.colToLetters(27));
        assertEquals("AB", CsvEvaluator.colToLetters(28));
        assertEquals("ZY", CsvEvaluator.colToLetters(701));
        assertEquals("ZZ", CsvEvaluator.colToLetters(702));
    }

    @Test
    @DisplayName("Column labels for columns 703-18278 should consist of the correct three letters")
    void testColToLetters3() {
        assertEquals("AAA", CsvEvaluator.colToLetters(703));
        assertEquals("AAB", CsvEvaluator.colToLetters(704));
        assertEquals("AMJ", CsvEvaluator.colToLetters(1024));
        assertEquals("XFD", CsvEvaluator.colToLetters(16384));
        assertEquals("ZZY", CsvEvaluator.colToLetters(18277));
        assertEquals("ZZZ", CsvEvaluator.colToLetters(18278));
    }

    @Test
    @DisplayName("A spreadsheet containing only constants should not be modified when evaluating " +
            "its formulas")
    void testEvaluateCsvConstant() throws IOException {
        String input = "x,1.5\n";
        String expected = "x,1.5\n";

        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
    }

    @Test
    @DisplayName("A spreadsheet with a formula referencing a cell on a previous row should " +
            "evaluate correctly.")
    void testEvaluateCsvAboveRef() throws IOException {
        String input = "x,1.5\n" +
                "y,=B1 4 * 1 +\n";
        String expected = "x,1.5\n"
                + "y,7.0\n";

        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
        output.toString();
    }

    @Test
    @DisplayName("A spreadsheet with a formula referencing a previous cell on the same row " +
            "should evaluate correctly.")
    void testEvaluateCsvLeftRef() throws IOException {
        String input = "x,1.5,=B1 4 * 1 +\n";
        String expected = "x,1.5,7.0\n";

        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
    }

    @Test
    @DisplayName("A spreadsheet with a formula referencing a previous formula should evaluate " +
            "correctly.")
    void testEvaluateCsvFormulaRef() throws IOException {
        String input = "x,1.5\n" +
                "y,=B1 4 * 1 +\n" +
                "z,=B1 B2 *\n";
        String expected = "x,1.5\n"
                + "y,7.0\n"
                + "z,10.5\n";

        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
    }

    @Test
    @DisplayName("A spreadsheet formula referencing a cell that does not contain a number should " +
            "evaluate to #N/A.")
    void testEvaluateCsvNonNumericRef() throws IOException {
        String input = "x,1.5\n" +
                "w,=A1\n";
        String expected = "x,1.5\n"
                + "w,#N/A\n";

        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
    }

    @Test
    void testKnownFunctionApps() throws IOException {
        String input = "x,=4 sqrt()\n"
                + "y,=B1 abs()";
        String expected = "x,2.0\n"
                + "y,2.0\n";
        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
    }

    @Test
    void testUnknownFunctionApps() throws IOException {
        String input = "x,=4 sqrt()\n"
                + "y,=B1 bar()";
        String expected = "x,2.0\n"
                + "y,#N/A\n";
        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
    }

    @Test
    void testFutureCellRefs() throws IOException {
        String input = "x,=4 sqrt()\n"
                + "y,=B3 abs()\n"
                + "z,=9 sqrt()";
        String expected = "x,2.0\n"
                + "y,#N/A\n"
                + "z,3.0\n";
        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
    }

    @Test
    void testOutOfBoundCells() throws IOException {
        String input = "x,=4 sqrt()\n"
                + "y,=B3 abs()";
        String expected = "x,2.0\n"
                + "y,#N/A\n";
        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
    }

    @Test
    void testNonCellVariables() throws IOException {
        String input = "x,=4 sqrt()\n"
                + "y,=notacell abs()";
        String expected = "x,2.0\n"
                + "y,#N/A\n";
        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
    }

    @Test
    void incompleteRpnFormula() throws IOException {
        String input = "x,=4 sqrt()\n"
                + "y,=B1 4 abs()";
        String expected = "x,2.0\n"
                + "y,#N/A\n";
        StringBuilder output = new StringBuilder();
        CsvEvaluator.evaluateCsv(CsvEvaluator.SIMPLIFIED_CSV.parse(new StringReader(input)),
                CsvEvaluator.SIMPLIFIED_CSV.print(output));
        assertEquals(expected, output.toString());
    }
}
