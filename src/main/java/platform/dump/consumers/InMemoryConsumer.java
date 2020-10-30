package platform.dump.consumers;

import objects.AbstractObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InMemoryConsumer<T extends AbstractObject> extends AbstractObjectConsumer<T> {

    List<T> buffer;

    public InMemoryConsumer(List<T> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void accept(T object) {
        buffer.add(object);
    }

}
