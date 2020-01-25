package search.request;

public class ConverterUtils {

    public enum RequestType {
        OR_COMPLEX_REQUEST,
        AND_COMPLEX_REQUEST
    }

    public static String stripSearchRequestString(String input) {
        if (null == input || input.isEmpty()) {
            return input;
        }
        if (!checkIfParenthesisBalanced(input)) {
            return null;
        }
        int leftPosition = 0, rightPosition = input.length() - 1;
        for (int i = 0; i < input.length(); i++) {
            if ('(' == input.charAt(i)) {
                leftPosition = i;
            } else {
                break;
            }
        }
        for (int i = input.length() - 1; i >= 0; i--) {
            if (')' == input.charAt(i)) {
                rightPosition = i;
            } else {
                break;
            }
        }
        if (0 == leftPosition && input.length() - 1 == rightPosition) {
            return input;
        }
        return input.substring(leftPosition, rightPosition + 1);
    }

    public static boolean checkIfParenthesisBalanced(String input) {
        if (input.isEmpty()) {
            return true;
        }
        int counter = 0;
        for (int i = 0; i < input.length(); i++) {
            if ('(' == input.charAt(i)) {
                counter++;
            } if (')' == input.charAt(i)) {
                counter--;
                if (counter < 0) {
                    return false; //there is an closing brace when nothing to close
                }
            }
        }
        return 0 == counter;
    }

    public static String skipN(String input, int n) {
        if (n > input.length()) {
            return null;
        } else {
            return input.substring(n);
        }
    }

    public static String skipParenthesis(String input) {
        if (input.startsWith("(") && input.endsWith(")")) {
            return input.substring(1, input.length() - 1);
        }
        return null;
    }

    public static int findConditionBorder(String input) {
        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if ('(' == input.charAt(i)) {
                count++;
            } else if (')' == input.charAt(i)) {
                count--;
                if (0 == count) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

}
