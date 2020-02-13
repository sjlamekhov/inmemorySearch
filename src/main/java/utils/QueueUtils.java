package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class QueueUtils {

    public static <T> List<T> extractFromQueue(Queue<T> source) {
        T message = source.poll();
        List<T> result = new ArrayList<>();
        while (message != null) {
            result.add(message);
            message = source.poll();
        }
        return result;
    }

}
