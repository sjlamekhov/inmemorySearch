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

    public String generateId() {
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() < length) {
            stringBuilder.append(Integer.toHexString(RandomUtils.nextInt()));
        }
        return stringBuilder.substring(0, length);
    }

    public int getLength() {
        return length;
    }

}
