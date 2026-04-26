package citadels;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the InputParser class.
 * Verifies correct behavior of range-prompting and input validation.
 */
public class InputParserTest {

    // Tests that a valid numeric input within the specified range is accepted and returned.
    @Test
    public void testValidInputInRange() {
        String simulatedInput = "5\n";
        InputParser parser = new InputParser(new ByteArrayInputStream(simulatedInput.getBytes()));

        int result = parser.promptIntInRange("Enter number: ", 4, 7);
        assertEquals(5, result);
    }

    // Tests that invalid inputs (non-numeric and out-of-range) are rejected before accepting a valid value.
    @Test
    public void testInvalidThenValidInput() {
        String simulatedInput = "hello\n10\n6\n";
        InputParser parser = new InputParser(new ByteArrayInputStream(simulatedInput.getBytes()));

        int result = parser.promptIntInRange("Enter number: ", 4, 7);
        assertEquals(6, result);
    }

    // Tests that an out-of-range input below the minimum is rejected, and a valid value is accepted afterward.
    @Test
    public void testInputBelowLowerBoundThenValid() {
        String simulatedInput = "3\n5\n";
        InputParser parser = new InputParser(new ByteArrayInputStream(simulatedInput.getBytes()));

        int result = parser.promptIntInRange("Enter number: ", 4, 7);
        assertEquals(5, result);
    }

    // Tests that promptLine returns the next input line correctly.
    @Test
    public void testPromptLineReturnsCorrectInput() {
        String simulatedInput = "hello world\n";
        InputParser parser = new InputParser(new ByteArrayInputStream(simulatedInput.getBytes()));

        String result = parser.promptLine("Say something: ");
        assertEquals("hello world", result);
    }

    // Tests that waitForCommand loops until the correct command is entered.
    @Test
    public void testWaitForCommandAcceptsCorrectInputEventually() {
        String simulatedInput = "wrong\nnope\ncontinue\n";
        InputParser parser = new InputParser(new ByteArrayInputStream(simulatedInput.getBytes()));

        parser.waitForCommand("continue");

    }
}
