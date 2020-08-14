package platform.dump;

import com.google.common.collect.AbstractIterator;
import objects.AbstractObject;

import java.util.function.Consumer;

public class DumpContext {

    AbstractIterator<AbstractObject> abstractIterator;
    Long timestampOfStart;
    Consumer<AbstractObject> objectConsumer;

    public DumpContext(
            AbstractIterator<AbstractObject> abstractIterator,
            Long timestampOfStart,
            Consumer<AbstractObject> objectConsumer) {
        this.abstractIterator = abstractIterator;
        this.timestampOfStart = timestampOfStart;
        this.objectConsumer = objectConsumer;
    }
}
