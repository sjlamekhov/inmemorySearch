package search.closestTo;

import objects.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DocumentToCoordinatesCalculator {

    public static Map<String, Integer> documentToCoordinate(Document document) {
        Objects.requireNonNull(document);
        Objects.requireNonNull(document.getAttributes());
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, String> attributeEntry : document.getAttributes().entrySet()) {
            result.put(attributeEntry.getKey(), getCoordinateOfString(attributeEntry.getValue()));
        }
        return result;
    }

    private static Integer getCoordinateOfString(String value) {
        int accumulator = 0;
        int multiplier = 1;
        for (int i = 0; i < value.length(); i++) {
            accumulator += value.charAt(i) * multiplier;
            multiplier *= 10;
        }
        return accumulator;
    }

}
