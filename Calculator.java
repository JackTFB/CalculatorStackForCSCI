import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Calculator {
    // Constants
    private static final String POSTFIX_OUTPUT_PATH = "infix_to_postfix_results.txt";
    private static final String EVALUATION_OUTPUT_PATH = "postfix_evaluation_results.txt";

    // PrintWriters for printing to output files
    private PrintWriter postfixOutput;
    private PrintWriter evalOutput;

    // Expects String arguments to be provided as mathematical equations in infix form. Converts
    // from infix to postfix form and evaluates the expression. Output will be printed to the
    // console and to files specified with constants POSTFIX_OUTPUT_PATH and EVALUATION_OUTPUT_PATH.
    public static void main(String[] args) {
        Calculator calc = new Calculator();

        // Loop through args
        // Each argument provided is expected to be a mathematical equation in infix form
        for (int i = 0; i < args.length; i++) {
            // Convert from infix to postfix
            String[] postfix = Calculator.infixToPostfix(Calculator.tokenize(args[i]));

            // Evaluate postfix
            double eval = evaluatePostfix(postfix);

            // Output to postfix output file
            calc.output(calc.postfixOutput, String.format("Input String: \"%s\"", args[i]));
            calc.output(calc.postfixOutput, String.format("Postfix Form: %s", Calculator.toString(postfix)));

            // Output to evaluation output file
            calc.output(calc.evalOutput, String.format("Input String: \"%s\"", args[i]));
            calc.output(calc.evalOutput, String.format("Postfix Form: %s", Calculator.toString(postfix)));
            calc.output(calc.evalOutput, String.format("Answer: %.3f", eval));
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

    // Returns a double. Mathematically evaluates a postfix equation.
    public static double evaluatePostfix(String[] postfix) {

        // TO DO
        Stack<String> stack = new Stack<String>();

        double evaluation = 0.0;

        double tempValue = 0.0;
        double[] tempDoubleArr = new double[2];

        for (int i = 0; i < postfix.length; i++)
        {
            //Check if current input is a number
            if (Pattern.matches("[\\d]+[.]?[\\d]*", postfix[i])){
                stack.push(postfix[i]);
            }
            else {
                switch (postfix[i]) {
                    case "+":
                        if (stack.size() > 0) {
                            tempDoubleArr[0] = Double.parseDouble(stack.pop());
                            tempDoubleArr[1] = Double.parseDouble(stack.pop());
                            tempValue = tempDoubleArr[1] + tempDoubleArr[0];
                            evaluation = tempValue;
                            stack.push(Double.toString(tempValue));
                        }
                        break;
                        
                    case "-":
                        if (stack.size() > 0) {
                            tempDoubleArr[0] = Double.parseDouble(stack.pop());
                            tempDoubleArr[1] = Double.parseDouble(stack.pop());
                            tempValue = tempDoubleArr[1] - tempDoubleArr[0];
                            evaluation = tempValue;
                            stack.push(Double.toString(tempValue));
                        }
                        break;
                    case "*":
                        if (stack.size() > 0) {
                            tempDoubleArr[0] = Double.parseDouble(stack.pop());
                            tempDoubleArr[1] = Double.parseDouble(stack.pop());
                            tempValue = tempDoubleArr[1] * tempDoubleArr[0];
                            evaluation = tempValue;
                            stack.push(Double.toString(tempValue));
                        }
                        break;
                    case "/":
                        if (stack.size() > 0) {
                            tempDoubleArr[0] = Double.parseDouble(stack.pop());
                            tempDoubleArr[1] = Double.parseDouble(stack.pop());
                            tempValue = tempDoubleArr[1] / tempDoubleArr[0];
                            evaluation = tempValue;
                            stack.push(Double.toString(tempValue));
                        }
                        break;
                    default:
                        break;
                    
                }
            }
        }

        return evaluation;

    }

        
    

    // Returns a String array. Converts input String array in infix form to postfix.
    public static String[] infixToPostfix(String[] infix) {
        LinkedList<String> postfixList = new LinkedList<String>();
        Stack<String> stack = new Stack<String>();

        for (int i = 0; i < infix.length; i++) {
            // Check if current input is a number
            if (Pattern.matches("[\\d]+[.]?[\\d]*", infix[i])) {
                postfixList.add(infix[i]);
            }
            else {
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
                    default:
                        // TO DO: THROW CUSTOM EXCEPTION HERE
                        break;
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
    }

    // Returns a String array. Utilized to tokenize a raw input String, breaking it up into String
    // tokens with only relevant values remaining (removes space and tab characters).
    public static String[] tokenize(String input) {
        // Tokenize the input String
        StringTokenizer tokensRaw = new StringTokenizer(input, " \t+-*/()", true);

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

    // Returns a String. Converts a String array to a printable String seperated by spaces.
    private static String toString(String[] array) {
        String output = "";
        for (int i = 0; i < array.length - 1; i++) {
            output = output + array[i] + " ";
        }
        output = output + array[array.length - 1];
        return output;
    }

    // Method for printing output to files and console.
    private void output(PrintWriter writer, String str) {
        if (writer != null) {
            writer.println(str);
        }
        System.out.println(str);
    }

    // TO DO: IMPLEMENT CUSTOM EXCEPTION
}