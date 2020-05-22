package search.closestTo;

import objects.Document;

import java.util.*;

public class DocumentToCoordinatesCalculator {

    private static final Map<Character, Integer> DICTIONARY = new HashMap() {{
        put('a', 'a' - 'a' + 1);
        put('b', 'b' - 'a' + 1);
        put('c', 'c' - 'a' + 1);
        put('d', 'd' - 'a' + 1);
        put('e', 'e' - 'a' + 1);
        put('f', 'f' - 'a' + 1);
        put('g', 'g' - 'a' + 1);
        put('h', 'h' - 'a' + 1);
        put('i', 'i' - 'a' + 1);
        put('j', 'j' - 'a' + 1);
        put('k', 'k' - 'a' + 1);
        put('l', 'l' - 'a' + 1);
        put('m', 'm' - 'a' + 1);
        put('n', 'n' - 'a' + 1);
        put('o', 'o' - 'a' + 1);
        put('p', 'p' - 'a' + 1);
        put('q', 'q' - 'a' + 1);
        put('r', 'r' - 'a' + 1);
        put('s', 's' - 'a' + 1);
        put('t', 't' - 'a' + 1);
        put('u', 'u' - 'a' + 1);
        put('v', 'v' - 'a' + 1);
        put('w', 'w' - 'a' + 1);
        put('x', 'x' - 'a' + 1);
        put('y', 'y' - 'a' + 1);
        put('z', 'z' - 'a' + 1);

        put('A', 'A' - 'A' + 27);
        put('B', 'B' - 'A' + 27);
        put('C', 'C' - 'A' + 27);
        put('D', 'D' - 'A' + 27);
        put('E', 'E' - 'A' + 27);
        put('F', 'F' - 'A' + 27);
        put('G', 'G' - 'A' + 27);
        put('H', 'H' - 'A' + 27);
        put('I', 'I' - 'A' + 27);
        put('J', 'J' - 'A' + 27);
        put('K', 'K' - 'A' + 27);
        put('L', 'L' - 'A' + 27);
        put('M', 'M' - 'A' + 27);
        put('N', 'N' - 'A' + 27);
        put('O', 'O' - 'A' + 27);
        put('P', 'P' - 'A' + 27);
        put('Q', 'Q' - 'A' + 27);
        put('R', 'R' - 'A' + 27);
        put('S', 'S' - 'A' + 27);
        put('T', 'T' - 'A' + 27);
        put('U', 'U' - 'A' + 27);
        put('V', 'V' - 'A' + 27);
        put('W', 'W' - 'A' + 27);
        put('X', 'X' - 'A' + 27);
        put('Y', 'Y' - 'A' + 27);
        put('Z', 'Z' - 'A' + 27);

        put('0', '0' - '0' + 53);
        put('1', '1' - '0' + 53);
        put('2', '2' - '0' + 53);
        put('3', '3' - '0' + 53);
        put('4', '4' - '0' + 53);
        put('5', '5' - '0' + 53);
        put('6', '6' - '0' + 53);
        put('7', '7' - '0' + 53);
        put('8', '8' - '0' + 53);
        put('9', '9' - '0' + 53);
    }};

    public static Map<String, Integer> documentToCoordinates(Document document, Set<String> allowedFields) {
        Objects.requireNonNull(document);
        Objects.requireNonNull(document.getAttributes());
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, String> attributeEntry : document.getAttributes().entrySet()) {
            if (!allowedFields.contains(attributeEntry.getKey())) {
                continue;
            }
            result.put(attributeEntry.getKey(), getCoordinateOfString(attributeEntry.getValue()));
        }
        return result;
    }

    //TODO: think about trailing spaces
    public static int getCoordinateOfString(String value) {
        int accumulator = 0;
        int multiplier = 1;
        int dictionarySize = DICTIONARY.size();
        int length = value.length();
        for (int i = 0; i < length; i++) {
            accumulator += DICTIONARY.getOrDefault(value.charAt(length - i - 1), dictionarySize + 1) * multiplier;
            multiplier *= dictionarySize;
        }
        return accumulator;
    }

    private static Set<List<String>> generateAllAttributeCombinations(List<String> attributeNames) {
        if (attributeNames.isEmpty()) {
            return Collections.emptySet();
        }
        Set<List<String>> result = new TreeSet<>(Comparator.comparingInt(List::size));
        int iterationsCount = 1 << attributeNames.size();   //2 ^ attributeNames.size()
        List<String> internal;
        for (int i = 1; i <= iterationsCount; i++) {
            internal = new ArrayList<>(attributeNames.size());
            for (int j = 0; j < attributeNames.size(); j++) {
                if ((i & (1 << j)) != 0) {
                    internal.add(attributeNames.get(j));
                }
            }
            if (!internal.isEmpty()) {
                result.add(internal);
            }
        }
        return result;
    }

}
