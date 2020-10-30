package platform.dump;

import objects.AbstractObject;
import platform.dump.consumers.AbstractObjectConsumer;

import java.util.function.Consumer;

public class DumpContext <T extends AbstractObject> {

    private final String dumpProcessId;
    private AbstractObjectConsumer objectConsumer;
    private boolean isFinished = false;
    private Long timestampOfStart, timestampOfFinish;

    public DumpContext(
            String dumpProcessId,
            Long timestampOfStart,
            AbstractObjectConsumer objectConsumer) {
        this.dumpProcessId = dumpProcessId;
        this.timestampOfStart = timestampOfStart;
        this.objectConsumer = objectConsumer;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public Long getTimestampOfStart() {
        return timestampOfStart;
    }

    public Long getTimestampOfFinish() {
        return timestampOfFinish;
    }

    public String getDumpProcessId() {
        return dumpProcessId;
    }

    public void finish() {
        isFinished = true;
        timestampOfFinish = System.currentTimeMillis();
        objectConsumer.finalHook();
    }

}
