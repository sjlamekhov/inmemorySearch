package search.editDistance;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EditGenerator {

    private static final char[] alphabet = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z'
    };

    public static Set<String> generateAllEdits(String input, int maxEdits) {
        Set<String> result = new HashSet<>();
        result.add(input.toLowerCase());

        Set<Edit> previousSet = Collections.singleton(new Edit(input.toLowerCase(), Collections.emptySet()));
        for (int i = 1; i <= maxEdits; i++) {
            previousSet = generateAllEditsIternal(previousSet);
            for (Edit previousEdit : previousSet) {
                result.add(previousEdit.getStringValue());
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Set<String> edits = generateAllEdits("input", 2);
        System.out.println(edits);
        System.out.println(edits.size());
    }

    private static Set<Edit> generateAllEditsIternal(Set<Edit> input) {
        Set<Edit> result = new HashSet<>();
        for (Edit edit : input) {
            if (edit.canBeEdittedOneMoreTime()) {
                String stringToEdit = edit.getStringValue();
                Set<Integer> newEditPositions = edit.getNewEditPositions();
                for (Integer newEditPosition : newEditPositions) {
                    StringBuilder stringBuilder = new StringBuilder(stringToEdit);

                    Set<Integer> editedPositions = new HashSet<>(edit.getEditedChars());
                    editedPositions.add(newEditPosition);

                    final char original = stringToEdit.charAt(newEditPosition);
                    for (char newValueToInsert : alphabet) {
                        if (newValueToInsert == original) {
                            continue;
                        }
                        stringBuilder.setCharAt(newEditPosition, newValueToInsert);
                        result.add(new Edit(stringBuilder.toString(), editedPositions));
                    }
                }
            }
        }
        return result;
    }

    private static class Edit {
        String stringValue;
        Set<Integer> editedChars;
        Set<Integer> newEditPositions;
        private boolean canBeEditedOneMoreTime;

        public Edit(String stringValue, Set<Integer> editedChars) {
            this.stringValue = stringValue;
            this.editedChars = editedChars;
            this.newEditPositions = findNewEditPositions();
            this.canBeEditedOneMoreTime = !newEditPositions.isEmpty();
        }

        private Set<Integer> findNewEditPositions() {
            Set<Integer> newEditPositions = new HashSet<>();
            for (int i = 0; i < stringValue.length(); i++) {
                if (!editedChars.contains(i)) {
                    newEditPositions.add(i);
                }
            }
            return newEditPositions;
        }

        private boolean isCanBeEditedOneMoreTime() {

            return false;
        }

        public String getStringValue() {
            return stringValue;
        }

        public Set<Integer> getEditedChars() {
            return editedChars;
        }

        public boolean canBeEdittedOneMoreTime() {
            return canBeEditedOneMoreTime;
        }

        public Set<Integer> getNewEditPositions() {
            return newEditPositions;
        }
    }

}
