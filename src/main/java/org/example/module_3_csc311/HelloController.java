package org.example.module_3_csc311;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.*;


public class HelloController {


    public Button verifyButton;
    public Button refreshButton;
    @FXML private ImageView cardOne, cardTwo, cardThree, cardFour;
    @FXML private TextField inputField;
    @FXML private TextField resultField;

    private List<Integer> currentCardValues; // Stores the values of the current cards

    /**
     * This is used in order to identify the value of the playing cards from the cards
     * folder.
     */
    private static final String[] RANKS = {
            "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"
    };
    /**
     * This is used in order to identify the set of which the playing cards are from
     */
    private static final String[] SUITS = {"hearts", "diamonds", "clubs", "spades"};

    /**
     * Starts the game with 4 randomized cards.
     */
    @FXML
    public void initialize() {
        generateNewCards();
    }

    /**
     * Alerts the user for errors made when entering expressions
     * Case 1: User doesn't enter an expression and presses verify
     * Case 2: User doesn't enter the all the values or the incorrect values in
     * the expression
     *
     * If it satisfies these conditions, it will test if the expression is equivalent to 24 and gives
     * either an error message or congratulatory message accordingly.
     */
    @FXML
    private void onVerify() {
        String expression = inputField.getText();
        if (expression == null || expression.isEmpty()) {
            showAlert("Error", "Please enter an expression.");
            return;
        }

        if (!validateExpression(expression)) {
            showAlert("Error", "Invalid expression! Use the correct numbers.");
            return;
        }

        try {
            double result = evaluateExpression(expression);
            if (result == 24) {
                resultField.setText("Correct! Expression evaluates to 24.");
            } else {
                resultField.setText("Incorrect! Try again.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid math expression.");
        }
    }

    /**
     * Clears the expression in the input field and changes the cards to a new set of 4 random cards
     */
    @FXML
    private void onRefresh() {
        generateNewCards();
        resultField.setText("");
        inputField.clear();
    }

    /**
     * Creates a new arraylist for currentCardValues
     * Assigns 4 new random cards that enter the currentCardValues array
     */
    private void generateNewCards() {
        Random rand = new Random();
        currentCardValues = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            String rank = RANKS[rand.nextInt(RANKS.length)];
            String suit = SUITS[rand.nextInt(SUITS.length)];
            int value = getCardValue(rank);
            currentCardValues.add(value);

            // Load and display card images
            String imagePath = "/cards/" + rank + "_of_" + suit + ".png";
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            switch (i) {
                case 0 -> cardOne.setImage(image);
                case 1 -> cardTwo.setImage(image);
                case 2 -> cardThree.setImage(image);
                case 3 -> cardFour.setImage(image);
            }
        }
    }

    /**
     * Assigns a numeric value to cards with string values
     * @param rank
     * @return
     */
    private int getCardValue(String rank) {
        return switch (rank) {
            case "ace" -> 1;
            case "jack" -> 11;
            case "queen" -> 12;
            case "king" -> 13;
            default -> Integer.parseInt(rank);
        };
    }

    /**
     *Compares the card numbers with the numbers used in the user entered expression
     * @param expression
     * @return
     */
    private boolean validateExpression(String expression) {
        List<Integer> usedNumbers = extractNumbers(expression);
        List<Integer> cardValues = new ArrayList<>(currentCardValues);

        Collections.sort(usedNumbers);
        Collections.sort(cardValues);

        System.out.println("Used Numbers: " + usedNumbers);
        System.out.println("Card Values: " + cardValues);

        return usedNumbers.equals(cardValues);
    }

    /**
     *Gets the numbers that are listed in the user's expression
     * @param expression
     * @return List<Integer>
     */
    private List<Integer> extractNumbers(String expression) {
        List<Integer> numbers = new ArrayList<>();
        StringBuilder numberBuilder = new StringBuilder();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c)) {
                numberBuilder.append(c);
            } else if (!numberBuilder.isEmpty()) {
                numbers.add(Integer.parseInt(numberBuilder.toString()));
                numberBuilder.setLength(0);
            }
        }
        if (!numberBuilder.isEmpty()) {
            numbers.add(Integer.parseInt(numberBuilder.toString()));
        }

        return numbers;
    }

    /**
     * Handles Debugging
     * @param expression
     * @return double
     */
    private double evaluateExpression(String expression) {
        System.out.println("Evaluating Expression: " + expression);
        return processExpression(expression);

    }

    /**
     * Handles the processing of the expression.
     * -Handles whitespaces
     * -handles paraenthesis to know when operators start
     * @param expression
     * @return
     */
    private double processExpression(String expression) {
        // Removes all whitespaces from the expression
        expression = expression.replaceAll("\\s+", "");

        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            // If the character is a digit then parse the full number
            if (Character.isDigit(c) || c == '.') {
                StringBuilder numBuilder = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numBuilder.append(expression.charAt(i++));
                }
                i--; // Adjusted for the last increment
                numbers.push(Double.parseDouble(numBuilder.toString()));
            }
            // If the char is an opening parenthesis then push to the operators stack so its on top
            else if (c == '(') {
                operators.push(c);
            }
            // If the character is a closing parenthesis then valuate the expression inside the parentheses
            else if (c == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop(); // Removes the '(' from the stack
            }
            // If the character is an operator then evaluate its place in PEMDAS
            else if (isOperator(c)) {
                while (!operators.isEmpty() && pemdas(operators.peek()) >= pemdas(c)) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
        }

        // Evaluate any remaining operations
        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        // The final result will be the only number left in the stack
        return numbers.pop();
    }

    /**
     * Checks for what operator is in the expression
     * @param c
     * @return
     */
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    /**
     * Integrates PEMDAS functionality
     * @param operator
     * @return
     */
    private int pemdas(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
        }
        return -1;
    }

    /**
     * Takes care of operators in the expression
     * @param operator
     * @param b
     * @param a
     * @return
     */
    private double applyOperation(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
        }
        throw new IllegalArgumentException("Invalid operator: " + operator);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}