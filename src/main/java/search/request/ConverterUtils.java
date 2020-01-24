package search.request;

public class ConverterUtils {

    public static String stripSearchRequestString(String input) {
        if (null == input || input.isEmpty()) {
            return input;
        }
        if (!checkIfParenthesisBalanced(input) || !checkIfValid(input)) {
            return null;
        }
        return "";
    }

    public static boolean checkIfParenthesisBalanced(String input) {
        if (input.isEmpty()) {
            return true;
        }
        int counter = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                counter++;
            } if (input.charAt(i) == ')') {
                counter--;
                if (counter < 0) {
                    return false; //there is an closing brace when nothing to close
                }
            }
        }
        return 0 == counter;
    }

    public static boolean checkIfValid(String input) {
        return true;
    }


}
