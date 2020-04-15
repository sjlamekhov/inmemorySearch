package search.editDistance;

public class LevenshteinDistance {

    public static int getDistance(String stringA, String stringB) {
        int m = stringA.length();
        int n = stringB.length();
        int[][] array = new int[m + 1][n + 1];

        array[0][0] = 0;
        for (int j = 1; j < n + 1; j++) {
            array[0][j] = array[0][j - 1] + 1;
        }
        for (int i = 1; i < m + 1; i++) {
            array[i][0] = array[i - 1][0] + 1;
            for (int j = 1; j < n + 1; j++) {
                array[i][j] = Math.min(
                        array[i - 1][j] + 1,
                        Math.min(
                                array[i][j - 1] + 1,
                                array[i - 1][j - 1] + (stringA.charAt(i - 1) != stringB.charAt(j - 1) ? 1 : 0)
                        )
                );
            }
        }
        return array[m][n];
    }

}
