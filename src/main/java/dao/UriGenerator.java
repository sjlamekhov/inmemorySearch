package dao;

import org.apache.commons.lang3.RandomUtils;

public class UriGenerator {

    private static final int DEFAULT_LENGTH = 12;
    private final int length;

    public UriGenerator() {
        this.length = DEFAULT_LENGTH;
    }

    public UriGenerator(int length) {
        this.length = length;
    }

    public static String generateId() {
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() < DEFAULT_LENGTH) {
            stringBuilder.append(Integer.toHexString(RandomUtils.nextInt()));
        }
        return stringBuilder.substring(0, DEFAULT_LENGTH);
    }

}
