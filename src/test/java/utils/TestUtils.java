package utils;

import java.util.function.Supplier;

public class TestUtils {

    public static void waitFor(Supplier<Boolean> toWaitFor) throws InterruptedException {
        waitFor(toWaitFor, 10000);
    }

    public static void waitFor(Supplier<Boolean> toWaitFor, long maxWait) throws InterruptedException {
        long startOfWaiting = System.currentTimeMillis();
        while (System.currentTimeMillis() - startOfWaiting < maxWait) {
            if (toWaitFor.get()) {
                break;
            }
            Thread.sleep(1000);
        }
    }

}
