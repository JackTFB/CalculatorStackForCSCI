import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Calculator {
    // Constants
    private static final String POSTFIX_OUTPUT_PATH = "infix_to_postfix_results.txt";
    private static final String EVALUATION_OUTPUT_PATH = "postfix_evaluation_results.txt";
    private static final String CALCULATOR_OUTPUT_PATH = "calculator_results.txt";

    // PrintWriters for printing to output files
    private PrintWriter postfixOutput;
    private PrintWriter evalOutput;
    private PrintWriter calcOutput;

    // Expects String command-line arguments to be provided as mathematical expressions in infix
    // form. Will also listen for additional String expressions to be provided via the console.
    // Converts from infix to postfix form and evaluates the expression. Output will be printed to
    // the console and to files specified with constants POSTFIX_OUTPUT_PATH,
    // EVALUATION_OUTPUT_PATH, and CALCULATOR_OUTPUT_PATH.
    public static void main(String[] args) {
        Calculator calc = new Calculator();

        // Loop through args
        // Each argument provided is expected to be a mathematical expression in infix form
        for (int i = 0; i < args.length; i++) {
            // Convert from infix to postfix
            String[] postfix = Calculator.infixToPostfix(args[i]);

            // Evaluate postfix
            double eval = evaluatePostfix(postfix);

            // Output
            calc.output(new PrintWriter[]{calc.postfixOutput, calc.evalOutput}, String.format("Input String: %s", args[i]));
            calc.output(new PrintWriter[]{calc.postfixOutput, calc.evalOutput}, String.format("Postfix Form: %s", Calculator.toString(postfix)));
            calc.output(new PrintWriter[]{calc.evalOutput}, String.format("Answer: %.3f", eval));
            calc.calcOutput.println(args[i]);
            calc.calcOutput.println(String.format("The input expression value is %.3f", eval));
        }

        // Accept user input from the console. While the user does not enter 'exit', program will continue.
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter a mathematical expression, or enter 'exit' to close the program:");
        String line = in.nextLine();
        while (!line.toLowerCase().contains("exit")) {
            // Convert from infix to postfix
            String[] postfix = Calculator.infixToPostfix(line);

            // Evaluate postfix
            double eval = evaluatePostfix(postfix);

            // Output
            calc.output(new PrintWriter[]{calc.postfixOutput, calc.evalOutput}, String.format("Input String: %s", line));
            calc.output(new PrintWriter[]{calc.postfixOutput, calc.evalOutput}, String.format("Postfix Form: %s", Calculator.toString(postfix)));
            calc.output(new PrintWriter[]{calc.evalOutput}, String.format("Answer: %.3f", eval));
            calc.calcOutput.println(line);
            calc.calcOutput.println(String.format("The input expression value is %.3f\n", eval));

            // Next line
            System.out.println("\nPlease enter a mathematical expression, or enter 'exit' to close the program:");
            line = in.nextLine();
        }
        in.close();
    }

    // Constructor
    public Calculator() {
        try {
            this.postfixOutput = new PrintWriter(new FileOutputStream(new File(POSTFIX_OUTPUT_PATH)), true);
            this.evalOutput = new PrintWriter(new FileOutputStream(new File(EVALUATION_OUTPUT_PATH)), true);
            this.calcOutput = new PrintWriter(new FileOutputStream(new File(CALCULATOR_OUTPUT_PATH)), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("\nUnable to create files for output. Please grant the necessary permissions.\nContinuing with output only being printed to console.");
            postfixOutput = null;
            evalOutput = null;
        }
    }

    // Returns a double. Mathematically evaluates a postfix equation.
    public static double evaluatePostfix(String[] postfix) {
        try {
            Stack<String> stack = new Stack<String>();
            double evaluation = 0.0;
            double tempValue = 0.0;
            double[] tempDoubleArr = new double[2];
            for (int i = 0; i < postfix.length; i++) {
                //Check if current input is a number
                if (Pattern.matches("[-]?[\\d]+[.]?[\\d]*", postfix[i])) {
                    stack.push(postfix[i]);
                } else {
                    switch (postfix[i]) {
                        case "+":
                            if (stack.size() > 1) {
                                tempDoubleArr[0] = Double.parseDouble(stack.pop());
                                tempDoubleArr[1] = Double.parseDouble(stack.pop());
                                tempValue = tempDoubleArr[1] + tempDoubleArr[0];
                                evaluation = tempValue;
                                stack.push(Double.toString(tempValue));
                            }
                            break;
                        case "-":
                            if (stack.size() > 1) {
                                tempDoubleArr[0] = Double.parseDouble(stack.pop());
                                tempDoubleArr[1] = Double.parseDouble(stack.pop());
                                tempValue = tempDoubleArr[1] - tempDoubleArr[0];
                                evaluation = tempValue;
                                stack.push(Double.toString(tempValue));
                            }
                            break;
                        case "*":
                            if (stack.size() > 1) {
                                tempDoubleArr[0] = Double.parseDouble(stack.pop());
                                tempDoubleArr[1] = Double.parseDouble(stack.pop());
                                tempValue = tempDoubleArr[1] * tempDoubleArr[0];
                                evaluation = tempValue;
                                stack.push(Double.toString(tempValue));
                            }
                            break;
                        case "/":
                            if (stack.size() > 1) {
                                tempDoubleArr[0] = Double.parseDouble(stack.pop());
                                tempDoubleArr[1] = Double.parseDouble(stack.pop());
                                tempValue = tempDoubleArr[1] / tempDoubleArr[0];
                                evaluation = tempValue;
                                stack.push(Double.toString(tempValue));
                            }
                            break;
                        default:
                            throw new InvalidExpressionException("The mathematical expression provided is invalid.");

                    }
                }
            }
        return evaluation;
        }
        catch (InvalidExpressionException e){
            System.err.println(e.getMessage());
            return 0.0;
        }
    }

    // Returns a String array. Converts input String in infix form to postfix.
    public static String[] infixToPostfix(String infixString) {
        try {
            String[] infix = tokenize(infixString);
            LinkedList<String> postfixList = new LinkedList<String>();
            Stack<String> stack = new Stack<String>();

            for (int i = 0; i < infix.length; i++) {
                // Check if current input is a number
                if (Pattern.matches("[-]?[\\d]+[.]?[\\d]*", infix[i])) {
                    postfixList.add(infix[i]);
                } else {
                    switch (infix[i]) {
                        case "(":
                            stack.push(infix[i]);
                            break;
                        case "+": case "-":
                            // While stack is not empty and top of stack is not "("
                            while (stack.size() > 0 && !stack.peek().equals("(")) {
                                postfixList.add(stack.pop());
                            }
                            stack.push(infix[i]);
                            break;
                        case "*": case "/":
                            // While stack is not empty and top of stack is not "(", "+", or "-"
                            // "+" and "-" have lower precedence than "*" and "/"
                            while (stack.size() > 0 && !stack.peek().equals("(") && !stack.peek().equals("+") && !stack.peek().equals("-")) {
                                postfixList.add(stack.pop());
                            }
                            stack.push(infix[i]);
                            break;
                        case ")":
                            // While stack is not empty and top of stack is not "("
                            while (stack.size() > 0 && !stack.peek().equals("(")) {
                                postfixList.add(stack.pop());
                            }
                            // Discard "("
                            if (stack.size() > 0) {
                                stack.pop();
                            }
                            break;
                        // The expression is invalid, so throw an excpetion
                        default:
                            throw new InvalidExpressionException("The mathematical expression provided is invalid.");
                    }
                }
            }
            // Pop anything left on stack and add to postfixList
            while (stack.size() > 0) {
                postfixList.add(stack.pop());
            }

            // Convert postfixList to a String array
            String[] postfixArray = new String[postfixList.size()];
            for (int i = 0; i < postfixArray.length; i++) {
                postfixArray[i] = postfixList.pop();
            }

            return postfixArray;

        // Handle InvalidExpressionException
        } catch (InvalidExpressionException e) {
            System.err.println(e.getMessage());
            return new String[1];
        }
    }

    // Returns a String array. Utilized to tokenize a raw input String, breaking it up into String
    // tokens with only relevant values remaining (removes space and tab characters).
    private static String[] tokenize(String input) {
        // Tokenize the input String
        StringTokenizer tokensRaw = new StringTokenizer(input, " \t+-*/()\"", true);

        // Loop through tokens and remove space and tab characters
        LinkedList<String> tokenList = new LinkedList<String>();
        while (tokensRaw.hasMoreTokens()) {
            String current = tokensRaw.nextToken();
            // Add current token to tokenList if it is not " " or "\t" (space or tab characters)
            if (!" \t\"".contains(current)) {

                // Logic to handle negative numbers
                if (current.equals("-")) {
                    // Previous token must be an operator or must be first token
                    if (tokenList.size() == 0 || "+-*/(".contains(tokenList.getLast())) {
                        // Next token must be a number
                        String next = tokensRaw.nextToken();
                        if (Pattern.matches("[\\d]+[.]?[\\d]*", next)) {
                            // Negative number
                            current = current + next;
                        } else {
                            // Not a negative number, so add current and set current to next
                            tokenList.add(current);
                            current = next;
                        }
                    }
                }

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

    // Returns a String. Converts a String array to a printable String seperated by spaces.
    private static String toString(String[] array) {
        String output = "";
        if (array.length > 0) {
            for (int i = 0; i < array.length - 1; i++) {
                output = output + array[i] + " ";
            }
            output = output + array[array.length - 1];
        }
        return output;
    }

    // Method for printing output to files and console.
    private void output(PrintWriter[] writers, String str) {
        for (int i = 0; i < writers.length; i++) {
            if (writers[i] != null) {
                writers[i].println(str);
            }
        }
        System.out.println(str);
    }

    // Custom exception for invalid expressions
    public static class InvalidExpressionException extends Exception {
        public InvalidExpressionException(String eMsg, Throwable e) {
            super(eMsg, e);
        }
        public InvalidExpressionException(String eMsg) {
            super(eMsg);
        }
    }
}