import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class Calculator {
    // Constants
    private static final String POSTFIX_OUTPUT_PATH = "infix_to_postfix_results.txt";
    private static final String EVALUATION_OUTPUT_PATH = "postfix_evaluation_results.txt";

    // PrintWriters for printing to output files
    private PrintWriter postfixOutput;
    private PrintWriter evalOutput;

    public static void main(String[] args) {
        Calculator calc = new Calculator();

        // Loop through args
        // Each argument provided is expected to be a mathematical equation in infix form
        for (int i = 0; i < args.length; i++) {
            // Convert from infix to postfix
            String[] postfix = Calculator.infixToPostfix(Calculator.tokenize(args[i]));

            // ADD EVALUATION OF POSTFIX HERE
            int eval = 0;
            // ADD EVALUATION OF POSTFIX HERE

            // Output to postfix output file
            calc.output(calc.postfixOutput, String.format("Input String: \"%s\"", args[i]));
            calc.output(calc.postfixOutput, String.format("Postfix Form: \"%s\"", Calculator.toString(postfix)));

            // Output to evaluation output file
            calc.output(calc.evalOutput, String.format("Input String: \"%s\"", args[i]));
            calc.output(calc.evalOutput, String.format("Postfix Form: \"%s\"", Calculator.toString(postfix)));
            calc.output(calc.evalOutput, String.format("Answer: %d", eval));
        }
    }

    // Constructor
    public Calculator() {
        try {
            this.postfixOutput = new PrintWriter(new FileOutputStream(new File(POSTFIX_OUTPUT_PATH)), true);
            this.evalOutput = new PrintWriter(new FileOutputStream(new File(EVALUATION_OUTPUT_PATH)), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("\nUnable to create files for output. Please grant the necessary permissions.\nContinuing with output only being printed to console.");
            postfixOutput = null;
            evalOutput = null;
        }
    }

    // Returns a String array. Converts input String array in infix form to postfix.
    public static String[] infixToPostfix(String[] infix) {
        String[] postfix = new String[infix.length];

        // TO DO

        return postfix;
    }

    // Returns a String array. Utilized to tokenize a raw input String, breaking it up into String
    // tokens with only relevant values remaining (removes space and tab characters).
    public static String[] tokenize(String input) {
        // Tokenize the input String
        StringTokenizer tokensRaw = new StringTokenizer(input, " \t+-*/=()", true);

        // Loop through tokens and remove space and tab characters
        LinkedList<String> tokenList = new LinkedList<String>();
        while (tokensRaw.hasMoreTokens()) {
            String current = tokensRaw.nextToken();
            // Add current token to tokenList if it is not " " or "\t" (space or tab characters)
            if (!current.equals(" ") && !current.equals("\t")) {
                tokenList.add(current);
            }
        }

        // Convert tokenList to a String array
        String[] tokenArray = new String[tokenList.size()];
        for (int i = 0; i < tokenArray.length; i++) {
            tokenArray[i] = tokenList.pop();
        }

        return tokenArray;
    }

    // Returns a String. Converts a String array to a printable String.
    private static String toString(String[] array) {
        String output = "";
        for (int i = 0; i < array.length; i++) {
            output = output + array[i];
        }
        return output;
    }

    // Method for printing output to files and console
    private void output(PrintWriter writer, String str) {
        if (writer != null) {
            writer.println(str);
        }
        System.out.println(str);
    }
}