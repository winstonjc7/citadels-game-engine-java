package citadels;

import java.io.InputStream;
import java.util.Scanner;

public class InputParser {
    private final Scanner scanner;

    public InputParser(InputStream in) {
        this.scanner = new Scanner(in);
    }

    public int promptIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public String promptLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void waitForCommand(String expectedCommand) {
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals(expectedCommand)) {
                break;
            } else {
                System.out.println("Invalid input. Type '" + expectedCommand + "' to continue.");
            }
        }
    }





    
}
