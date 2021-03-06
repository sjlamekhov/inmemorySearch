package search.closestTo;

import objects.AbstractObject;
import objects.Document;

import java.math.BigInteger;
import java.util.*;

public class DocumentToCoordinatesCalculator<T extends AbstractObject> {

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

    private Map<String, Long> documentToCoordinates(T document, Set<String> allowedFields) {
        Objects.requireNonNull(document);
        Objects.requireNonNull(document.getAttributes());
        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<String, String> attributeEntry : document.getAttributes().entrySet()) {
            if (!allowedFields.contains(attributeEntry.getKey())) {
                continue;
            }
            result.put(attributeEntry.getKey(), stringToCoordinate(attributeEntry.getValue()));
        }
        return result;
    }

    //TODO: think about trailing spaces
    public static long stringToCoordinate(String value) {
        BigInteger accumulator = BigInteger.valueOf(0L);
        BigInteger multiplier = BigInteger.valueOf(1L);
        BigInteger addition;
        int dictionarySize = DICTIONARY.size();
        int length = value.length();
        for (int i = 0; i < length; i++) {
            addition = BigInteger.valueOf(DICTIONARY.getOrDefault(value.charAt(length - i - 1), dictionarySize + 1));
            accumulator = accumulator.add(addition.multiply(multiplier));
            multiplier = multiplier.multiply(BigInteger.valueOf(dictionarySize));
        }
        return accumulator.longValue();
    }

    public Map<Set<String>, Long> combineAttributesAndCoordinates(T document, Set<String> allowedFields) {
        Map<String, Long> convertedDocument = documentToCoordinates(document, allowedFields);
        if (convertedDocument.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Long> squaresOfCoordinates = new HashMap<>(convertedDocument.size());
        for (Map.Entry<String, Long> entry : convertedDocument.entrySet()) {
            squaresOfCoordinates.put(entry.getKey(), (long) (entry.getValue() * entry.getValue()));
        }
        Set<Set<String>> attributeCombinations = generateAllAttributeCombinations(new ArrayList<>(convertedDocument.keySet()));
        Map<Set<String>, Long> result = new HashMap<>(attributeCombinations.size());
        for (Set<String> combination : attributeCombinations) {
            int tmpResult = 0;
            for (String attributeName : combination) {
                tmpResult += squaresOfCoordinates.get(attributeName);
            }
            result.put(combination, Math.round(Math.sqrt(tmpResult)));
        }
        return result;
    }

    private static Set<Set<String>> generateAllAttributeCombinations(List<String> attributeNames) {
        if (attributeNames.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Set<String>> result = new HashSet<>();
        int iterationsCount = 1 << attributeNames.size();   //2 ^ attributeNames.size()
        Set<String> internal;
        for (int i = 1; i <= iterationsCount; i++) {
            internal = new HashSet<>(attributeNames.size());
            for (int j = 1; j <= attributeNames.size(); j++) {
                if ((i & (1 << (j - 1))) != 0) {
                    internal.add(attributeNames.get(j - 1));
                }
            }
            if (!internal.isEmpty()) {
                result.add(internal);
            }
        }
        return result;
    }

}
